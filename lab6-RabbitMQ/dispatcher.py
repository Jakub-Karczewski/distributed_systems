import time
import pika
import itertools
from config_to_run_first import goods_to_suppliers

connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
channel = connection.channel()

channel.exchange_declare(exchange='orders', exchange_type='topic')

# dispatcher jest posrednikiem i przyjmuje zlecenia ekip na routing key sprzet.*

channel.queue_declare(queue='dispatcher')
channel.queue_bind(exchange='orders', queue='dispatcher', routing_key='sprzet.*')


# Round robin po dostawcach i na tej podstawie nowy slownik
round_robin = {k: itertools.cycle(v) for k, v in goods_to_suppliers.items()}


def callback(_ch, method, _properties, body):

    # Pole method zawiera routing key, w tym przypadku np "sprzet.tlen"

    thing_type = method.routing_key.split('.')[1]
    supplier = next(round_robin[thing_type])  # zwraca iterator aktualny i następnie zmienia na kolejny
    # Uwaga - round robin wykonuje osobno cykl dla kazdego sprzetu

    routing_key = f'dostawca.{supplier}'
    print(f"[dipatcher] {body.decode().split(":")[0]} -> {thing_type} -> {supplier}")

    _ch.basic_ack(delivery_tag=method.delivery_tag)
    channel.basic_publish(exchange='orders', routing_key=routing_key, body=body)
    #time.sleep(0.1)


channel.basic_consume(queue='dispatcher', on_message_callback=callback, auto_ack=False)
print("dispatcher waiting for teams to send jobs")
channel.start_consuming()
