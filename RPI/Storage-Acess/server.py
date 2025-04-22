from flask import Flask, request, send_from_directory, jsonify
import os
import time
import threading
from datetime import datetime
import cv2
import firebase_admin
from firebase_admin import credentials, messaging

app = Flask(__name__)
IMAGE_DIR = "img"                           # Diretorio onde as imagens estao armazenadas
FIREBASE_CREDENTIALS = "credentials.json"   # Ficheiro com credencias do firebase
STOP_MOTION_ACTIVE = False
stop_motion_thread = None

# Garante que o diretorio existe
os.makedirs(IMAGE_DIR, exist_ok=True)

# Inicializacao segura do Firebase
if not firebase_admin._apps:
    cred = credentials.Certificate(FIREBASE_CREDENTIALS)
    firebase_admin.initialize_app(cred)


# Funcao para enviar uma notificacao push usando Firebase Admin SDK
def send_notification():
	print('Sending notification')

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
		return True, response  # Retorna como esperado
	except Exception as e:
		print(f'Failed to send notification: {e}')
		return False, str(e)  # Retorna erro, mas nunca retorna None




def capture_stop_motion():
    """Captura imagens em stop-motion a cada 5 segundos por pelo menos 5 minutos."""
    global STOP_MOTION_ACTIVE
    STOP_MOTION_ACTIVE = True
    cap = cv2.VideoCapture(0)  # Abre a camera USB padrao
    if not cap.isOpened():
        print("Erro ao aceder a camera.")
        STOP_MOTION_ACTIVE = False
        return
    
    start_time = time.time()
    while STOP_MOTION_ACTIVE and (time.time() - start_time < 300):  # 5 minutos
        ret, frame = cap.read()
        if ret:
            timestamp = datetime.now().strftime("%Y%m%d%H%M%S")
            image_path = os.path.join(IMAGE_DIR, f"frame_{timestamp}.jpg")
            cv2.imwrite(image_path, frame)
        time.sleep(5)
    
    cap.release()
    STOP_MOTION_ACTIVE = False


@app.route("/notify", methods=["GET"])
def notify():
	success, result = send_notification()
	if success:
		return jsonify({"message": "Notificacao enviada com sucesso", "id": result}), 200
	else:
		return jsonify({"error": "Falha ao enviar notificacao", "details": result}), 500    
    
    
@app.route("/start_stop_motion", methods=["GET"])
def start_stop_motion():
    """Inicia a gravacao stop-motion."""
    global stop_motion_thread
    if STOP_MOTION_ACTIVE:
        return jsonify({"message": "Stop-motion ja esta a correr."}), 400
    stop_motion_thread = threading.Thread(target=capture_stop_motion)
    stop_motion_thread.start()
    return jsonify({"message": "Stop-motion iniciado."})

@app.route("/stop_stop_motion", methods=["GET"])
def stop_stop_motion():
    """Para a gravacao stop-motion."""
    global STOP_MOTION_ACTIVE
    STOP_MOTION_ACTIVE = False
    return jsonify({"message": "Stop-motion interrompido."})

@app.route("/images", methods=["GET"])
def list_images():
    """Retorna a lista de imagens disponiveis."""
    images = os.listdir(IMAGE_DIR)
    return jsonify(images)

@app.route("/images/<filename>", methods=["GET"])
def get_image(filename):
    """Serve uma imagem especifica."""
    return send_from_directory(IMAGE_DIR, filename)

@app.route("/images/<filename>", methods=["DELETE"])
def delete_image(filename):
    """Exclui uma imagem do servidor."""
    file_path = os.path.join(IMAGE_DIR, filename)
    if os.path.exists(file_path):
        os.remove(file_path)
        return jsonify({"message": "Imagem removida com sucesso."}), 200
    return jsonify({"error": "Imagem nao encontrada."}), 404

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=4000, debug=True)

