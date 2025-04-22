import cv2
import socket
import pickle
import struct

client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client_socket.connect(('192.168.1.235', 8888))  # Troque pelo IP do servidor

data = b""
payload_size = struct.calcsize("Q")

while True:
    while len(data) < payload_size:
        packet = client_socket.recv(64 * 1024)  # Aumentei o buffer para reduzir latência
        if not packet:
            break
        data += packet

    if not data:
        break

    packed_msg_size = data[:payload_size]
    data = data[payload_size:]
    msg_size = struct.unpack("Q", packed_msg_size)[0]

    while len(data) < msg_size:
        data += client_socket.recv(64 * 1024)

    frame_data = data[:msg_size]
    data = data[msg_size:]

    # Decodificar frame recebido
    frame = cv2.imdecode(pickle.loads(frame_data), cv2.IMREAD_COLOR)

    cv2.imshow('Cliente', frame)
    if cv2.waitKey(1) == 13:
        break

cv2.destroyAllWindows()
client_socket.close()
