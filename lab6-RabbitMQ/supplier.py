import pika
import sys
from config_to_run_first import suppliers

# supplier_name = sys.argv[1] if len(sys.argv) > 1 else 'dostawca1'
supplier_name = ""
while True:
    supplier_name = str(input)
    if supplier_name in suppliers:
        break
    print("[ADMIN] Nazwa niezgodna ze zdeklarowana lista")


def callback(_ch, method, _properties, body):
    if method.routing_key in ["dostawca.*", "#"]:
        print(body.decode())
        return

    if method.routing_key.split('.')[1] == supplier_name:
        team_id, order_id, thing = body.decode().split(':')  # all the necessary information are in body
        print(f"[{supplier_name}]  is handling the order: {order_id}, {thing} from {team_id}")
        confirm = f"{supplier_name} sending confirmation for {thing} : {order_id}"

        _ch.basic_ack(delivery_tag=method.delivery_tag)
        channel.basic_publish(exchange='orders', routing_key=f'ekipa.{team_id}', body=confirm)


connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
channel = connection.channel()
channel.exchange_declare(exchange='orders', exchange_type='topic')

routing_key = f'dostawca.{supplier_name}'
channel.queue_declare(queue=routing_key)

channel.queue_bind(exchange='orders', queue=routing_key, routing_key=routing_key)
channel.queue_bind(exchange='orders', queue=routing_key, routing_key='dostawca.*')
channel.queue_bind(exchange='orders', queue=routing_key, routing_key='#')

channel.basic_consume(queue=routing_key, on_message_callback=callback, auto_ack=False)

print(f"[{supplier_name}] waiting for dispatcher")
channel.start_consuming()
