import pika
import sys
import uuid
import time
from config_to_run_first import suppliers, goods, teams

# team_name = sys.argv[1] if len(sys.argv) > 1 else 'ekipa1'
team_name = ""

while True:
    team_name = str(input)
    if team_name in teams:
        break
    print("[ADMIN] Nazwa niezgodna ze zdeklarowana lista")


def callback(ch, method, properties, body):  # all arguments are needed, despite being usuned
    if method.routing_key in ["ekipa.*", "#"]:
        print(body.decode())
        ch.basic_ack(delivery_tag=method.delivery_tag)
        return

    if method.routing_key.split('.')[1] == team_name:
        print(f"[{team_name}]  received confirmation: {body.decode()}")
        ch.basic_ack(delivery_tag=method.delivery_tag)


connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
channel = connection.channel()

channel.exchange_declare(exchange='orders', exchange_type='topic')
routing_key = f'ekipa.{team_name}'
channel.queue_declare(queue=routing_key)

channel.queue_bind(exchange='orders', queue=routing_key, routing_key=routing_key)
channel.queue_bind(exchange='orders', queue=routing_key, routing_key='ekipa.*')
channel.queue_bind(exchange='orders', queue=routing_key, routing_key='#')

channel.basic_consume(queue=f'ekipa.{team_name}', on_message_callback=callback, auto_ack=False)

print(f"[{team_name}] is sending order")
for thing in goods:
    msg = f"{team_name}:{str(uuid.uuid4())[:4]}:{thing}"
    print(f"[{team_name}] orders {thing}")
    channel.basic_publish(exchange='orders', routing_key=f'sprzet.{thing}', body=msg)
    time.sleep(0.5)

print(f"[{team_name}] waiting for confirmations from suppliers")
channel.start_consuming()
