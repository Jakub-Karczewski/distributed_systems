import pika
import sys

def callback(ch, method, properties, body):
    print(f"[ADMIN] Przechwycono: {method.routing_key} -> {body.decode()}")
    #ch.basic_ack(delivery_tag=method.delivery_tag)


connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
channel = connection.channel()
channel.exchange_declare(exchange='orders', exchange_type='topic')

channel.queue_declare(queue='admin')
channel.queue_bind(exchange='orders', queue='admin', routing_key='#')
channel.basic_consume(queue='admin', on_message_callback=callback, auto_ack=True)

print("[ADMIN] collecting information")


def send_admin(msg, grupa):
    key = {
        'teams': 'ekipa.*',
        'suppliers': 'dostawca.*',
        'all': '#'
    }[grupa]
    print(f"[ADMIN] Wysyłam: {msg} do {grupa}")
    channel.basic_publish(exchange='orders', routing_key=key, body=f"[ADMIN] {msg}")


send_admin("Check the equipment", 'teams')
send_admin("New delivery tomorrow", 'suppliers')
send_admin("Will have a maintanance break tommorow at 17.", 'all')

channel.start_consuming()
