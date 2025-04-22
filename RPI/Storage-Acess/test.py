import firebase_admin
from firebase_admin import credentials, messaging

def send_notification():
	print('Sending notification')
	
	cred = credentials.Certificate('/home/rui/Desktop/SE/Projeto/Storage-Acess/credentials.json')
	firebase_admin.initialize_app(cred)

	message = messaging.Message(
		notification=messaging.Notification(
			title='Campainha',
			body='Alguem tocou na campainha'
		),
		topic='all'
	)
	messaging.send(message)
	
	try:
		# Envia e mostra o ID da mensagem
		response = messaging.send(message)
		print(f'Notification sent successfully. Message ID: {response}')
	except Exception as e:
		print(f'Failed to send notification: {e}')
	
send_notification()
