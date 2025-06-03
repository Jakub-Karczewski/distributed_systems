package sr.grpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import events.Event;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;


public class grpcServer 
{
	// Zadaniem loggera bedzie wysylanie powiadomien o stanie serwera
	private static final Logger logger = Logger.getLogger(grpcServer.class.getName());
	private String address = "127.0.0.5";
	private int port = 50051;
	private Server server;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private final Random random = new Random();
	private final String[] countries = {"Polska", "Niemcy", "Francja", "Hiszpania", "Wielka Brytania"};
	private final Event.InclineType[] InclineType = {Event.InclineType.NON, Event.InclineType.LOW, Event.InclineType.MEDIUM, Event.InclineType.HIGH};
	private final Event.RunType[] RunType = {Event.RunType.ANY, Event.RunType.STREET, Event.RunType.CROSS_COUNTRY, Event.RunType.MOUNTAINS, Event.RunType.ON_TRACK};
	private final Map<String, Map<String, StreamObserver<Event>>> subscriptions = new ConcurrentHashMap<>();
	private final Map<String, Map<String, Event>> messageBuffers = new ConcurrentHashMap<>();

	private void start() throws IOException 
	{
		//Korzystamy z sever Builder z gen, wybieramy pule watkow, na ktorych bedzie sie odbywac komunikacja i klase odpowiadajaca za implementacje
		//mającą metody onNext, onError, OnCOmpleted
		server = ServerBuilder.forPort(port).executor((Executors.newFixedThreadPool(16)))
				.addService(new EventServiceImpl(subscriptions, messageBuffers))
				.build()
				.start();
		logger.info("Server started, listening on " + port);

		// ustawiamy aby operacja losowania zdarzenia i przesylania ho odpowiednim subskrybentom odbywala sie stale co 1 sekunde
		scheduler.scheduleAtFixedRate(this::sendEvent, 0, 1, TimeUnit.SECONDS);

		//Wykona sie tuz przez zakonczeniem pracy serwera
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.err.println("Shutting down gRPC server...");
				grpcServer.this.stop();
				System.err.println("Server shut down.");
			}
		});
	}

	void sendEvent() {

		String country = countries[random.nextInt(countries.length)];
		Event.RunType runtype = RunType[random.nextInt(RunType.length)];
		Event.InclineType incltype;
		if (runtype == Event.RunType.ON_TRACK){
			incltype = Event.InclineType.NON;
		}
		else if(runtype == Event.RunType.MOUNTAINS){
			incltype = Event.InclineType.HIGH;
		}
		else{
			incltype = InclineType[random.nextInt(3)];
		}


		// Przeszukuje Hashmape subskrybentow, znajduje tych ktorzy wybrali wylosowane miasto i zwraca ich liste
		List<String> subscribersIds = subscriptions.entrySet().stream()
				.filter(subscribedCities1 -> subscribedCities1.getValue().containsKey(country))
				.map(Map.Entry::getKey)
				.toList();

		int min = 3, max = 42;
		int distance = min + random.nextInt(max - min);
		// Z wykorzystaniem kodu generowanego w zakladce gen, tworzy nowy Payload z losowymi parametrami
		Event.Details details = Event.Details.newBuilder()
				.setDistanceKms(distance)
				.setInclinetype(incltype)
				.setDescription("Description for run in " + country)
				.build();

		// tak samo korzystajac z gen, z wykorzystanem klasy EventBuilder ustawiamy mu poprzednio przygotowane parametry
		Event.Builder eventBuilder = Event.newBuilder()
				.addAllSubscribedBy(subscribersIds)
				.setCountry(country)
				.setRuntype(runtype)
				.setDetails(details);

		// Finalnie tworzymy event ktory zostanie wyslany do subskrybentow
		Event event = eventBuilder.build();

		subscriptions.entrySet().forEach((Cities -> {
			StreamObserver<Event> obs = Cities.getValue().get(country);
			if (obs != null) {
				try {
					obs.onNext(event);
				} catch (Exception e) {
					//W przypadku kiedy nie uda sie wyslac wiadomosci do klienta, np ze wzgledu na to, ze utracil polaczenie
					//Buforujemy przygotowane zdarzenie, aby wyslac do niego, gdy serwer dostanie informacje o ponowieniu polaczenia
					messageBuffers.computeIfAbsent(Cities.getKey(), k -> new HashMap<>()).put(country, event);
				}
			}
		}
		));
	}

	private void stop() {
		if (server != null) {
			server.shutdown();
		}
	}

	private void blockUntilShutdown() throws InterruptedException {
		if (server != null) {
			//blokuje watek glowny, aby serwer nie zamknal sie od razu
			server.awaitTermination();
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		final grpcServer server = new grpcServer();
		server.start();
		server.blockUntilShutdown();
	}

}
