import grpc
import uuid
import queue
import threading
import time

from streaming_pb2_grpc import EventServiceStub
from streaming_pb2 import ControlRequest, Subscribe, Unsubscribe, Reconnect

incline_type_map = {
    0: 'brak',
    1: 'mały',
    2: 'średni',
    3: 'wysoki'
}

run_type_map = {
    0: "brak",
    1: "Bieg uliczny",
    2: "Bieg przelajowy",
    3: "Bieg gorski",
    4: "Bieg na stadionie"
}

valid_countries = ["Polska", "Niemcy", "Francja", "Hiszpania", "Wielka Brytania"]


class Application:
    def __init__(self):
        self.stub = None
        #Requests zawiera zapytania - obiekty typu Control Request, ktore beda wyslane do serwera
        self.requests = None
        self.subscription_id = "2e1e1d66-41af-4f10-bef8-6a728fed0852"
        #str(uuid.uuid4()))
        #State przechowuje odpowiedzi typu Event od serwera na zapytania
        self.state = {}
        with open("responses.txt", "w") as f:
            f.write("")
        self.connect_to_server()
        self.get_requests_from_console()

    def get_requests_from_console(self):
        sub_str, unsub_str = ["subscribe", "sub"], ["unsubscribe", "unsub"]
        while True:
            print("Podaj typ operacji")
            req_type = str(input())
            if req_type.lower() not in sub_str + unsub_str:
                print("Nieobslugiwany typ operacji")
                continue
            print("Podaj kraj")
            country = str(input())
            if country not in valid_countries:
                print("Nieobslugiwana nazwa kraju")
                continue
            if req_type.lower() in unsub_str:
                self.unsubscribe(country)
            elif req_type.lower() in sub_str:
                self.subscribe(country)

    def subscribe(self, country):
        self.requests.put(Subscribe(subscription_id=self.subscription_id, country=country))

    def unsubscribe(self, country):
        self.requests.put(Unsubscribe(subscription_id=self.subscription_id, country=country))
        del self.state[country]

    def reconnect(self):
        self.requests.put(Reconnect(subscription_id=self.subscription_id))

    def send_control_requests(self):

        while True:
            #Jesli kolejka bedzie pusta, wtedy .get() spowoduje, ze funkcja zawiesi sie w oczekiwaniu na info
            request = self.requests.get()

            #yield pozwala wykonać zwracać wartości po kawałku, po kolei bez konieczności wywołania return, które zwróci od razu całość
            #Ma tutaj zastosowanie w kontekście strumienia danych

            if isinstance(request, Subscribe):
                yield ControlRequest(sub=request)
            elif isinstance(request, Unsubscribe):
                yield ControlRequest(unsub=request)
            else:
                yield ControlRequest(rec=request)

    def connect_to_server(self):
        self.requests = queue.Queue()

        def run():
            # W insecure channel dane uwierzytelniające nie są szyfrowane
            self.channel = grpc.insecure_channel('127.0.0.5:50051', options=[
                ('grpc.keepalive_time_ms', 60000),
                ('grpc.keepalive_timeout_ms', 20000),
                # Po to, aby w przypadku braku aktywnych polaczen nie zostalo ono szybko zmienione w stan idle
                ('grpc.keepalive_permit_without_calls', 1),
            ])
            # Stub umozliwia asynchroniczna komunikacje, bazuje na callback, nie blokuje watku
            self.stub = EventServiceStub(self.channel)

            self.reconnect()
            self.listen()

        threading.Thread(target=run, daemon=True).start()

    def listen(self):
        try:
            #Tworzy obiekt typu Stream Events ktory bedzie zawieral Control Requests
            responses = self.stub.StreamEvents(self.send_control_requests())
            for response in responses:
                self.state[response.country] = response
                with open("responses.txt", "a") as f:
                    f.write(f"kraj: {response.country}, Ilosc subskrybentow: {len(response.subscribed_by)}, " +
                            f"Typ biegu: {run_type_map[response.runtype]}, Dystans: {response.details.distance_kms}km, " +
                            f"Nachylenie trasy: {incline_type_map[response.details.inclinetype]}\n")

        except grpc.RpcError:
            print("Reconnecting...")
            self.state.clear()
            self.connect_to_server()


if __name__ == "__main__":
    Application()
