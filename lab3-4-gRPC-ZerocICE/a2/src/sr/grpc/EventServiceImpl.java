package sr.grpc;

import events.ControlRequest;
import events.Event;
import events.EventServiceGrpc;
import events.Subscribe;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EventServiceImpl extends EventServiceGrpc.EventServiceImplBase
{
	private final Map<String, Map<String, StreamObserver<Event>>> subscriptions;
	private final Map<String, Map<String, Event>> messageBuffers;

	public EventServiceImpl(Map<String, Map<String, StreamObserver<Event>>> subscriptions, Map<String, Map<String, Event>> messageBuffers) {
		this.subscriptions = subscriptions;
		this.messageBuffers = messageBuffers;
	}

	@Override
	public StreamObserver<ControlRequest> streamEvents(StreamObserver<Event> responseObserver) {
		return new StreamObserver<>() {
			private String clientID;
			@Override
			public void onNext(ControlRequest request) {
				if (request.hasSub()) {

					Subscribe subscribe = request.getSub();
					clientID = subscribe.getSubscriptionId();
					String country = subscribe.getCountry();

					subscriptions.computeIfAbsent(clientID, k -> new HashMap<>()).put(country, responseObserver);

					System.out.println("Subscribed: " + clientID + ", Country: " + country);

				} else if (request.hasUnsub()) {
					String id = request.getUnsub().getSubscriptionId();
					String country = request.getUnsub().getCountry();

					subscriptions.computeIfPresent(id, (k, SubCountries) -> {
						SubCountries.remove(country);
                        return SubCountries;
					});

					System.out.println("Unsubscribed: " + id + ", Country: " + country);

				}else if (request.hasRec()) {
					String id = request.getRec().getSubscriptionId();
					sendBufferedMessages(id, responseObserver);
					System.out.println("Reconnected: " + id);
				}
			}

			@Override
			public void onError(Throwable t) {
				cleanupClient();
				System.err.println("Client disconnected with error: " + t.getMessage());
			}


			private void cleanupClient() {
				if (clientID != null) {
                    subscriptions.remove(clientID);
					System.out.println("Cleaned up remaining subscriptions for client: " + clientID);
				}
			}

			@Override
			public void onCompleted() {
				System.out.println("Client completed");
				responseObserver.onCompleted();
			}
		};
	}

	public void sendBufferedMessages(String SubID, StreamObserver<Event> observer) {
		if (!subscriptions.containsKey(SubID)) {
			return;
		}

		Map<String, Event> bufferedEvents = messageBuffers.get(SubID);
		if (bufferedEvents != null && !bufferedEvents.isEmpty()) {
			System.out.println("Wysyłanie " + bufferedEvents.size() + " zbuforowanych wiadomości dla subskrypcji " + SubID);
			for (Event event : bufferedEvents.values()) {
				String eventCountry = event.getCountry();

				subscriptions.computeIfAbsent(SubID, k -> new HashMap<>()).put(eventCountry, observer);

				observer.onNext(event);
			}
			bufferedEvents.clear();
		}
	}

}

