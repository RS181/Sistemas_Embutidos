#include <WiFiS3.h>

// WiFi credentials
char ssid[] = "SSID";
char pass[] = "PASSWORD";
int status = WL_IDLE_STATUS;

// IP do servidor que irá receber os pedidos
IPAddress serverIP(192, 168, 1, 207); // <-- Alterar Ip para o servidor do RPI

WiFiClient client;

// Pinos
int BUTTON_PIN = 5;
int PIN_TO_SENSOR = 9;

int pinStateCurrent = LOW;
int pinStatePrevious = LOW;

int lastState = HIGH;
int currentState;

void setup() {
  Serial.begin(9600);
  while (!Serial);

  // Conexão WiFi
  if (WiFi.status() == WL_NO_MODULE) {
    Serial.println("Communication with WiFi module failed!");
    while (true);
  }

  String fv = WiFi.firmwareVersion();
  if (fv < WIFI_FIRMWARE_LATEST_VERSION) {
    Serial.println("Please upgrade the firmware");
  }

  while (status != WL_CONNECTED) {
    Serial.print("Connecting to SSID: ");
    Serial.println(ssid);
    status = WiFi.begin(ssid, pass);
    delay(10000);
  }

  Serial.println("Connected to WiFi!");
  printCurrentNet();
  printWifiData();

  // Inicialização dos pinos
  pinMode(BUTTON_PIN, INPUT_PULLUP);
  pinMode(PIN_TO_SENSOR, INPUT);
}

void loop() {
  currentState = digitalRead(BUTTON_PIN);
  pinStatePrevious = pinStateCurrent;
  pinStateCurrent = digitalRead(PIN_TO_SENSOR);

  if (pinStatePrevious == LOW && pinStateCurrent == HIGH) {
    Serial.println("Motion detected!");
    sendGET("/start_stop_motion");
  } else if (pinStatePrevious == HIGH && pinStateCurrent == LOW) {
    Serial.println("Motion stopped!");
  }

  if (lastState == LOW && currentState == HIGH) {
    Serial.println("Button pressed");
    sendGET("/notify");
  }

  lastState = currentState;
  delay(100);
}

// Função para enviar GET
void sendGET(String path) {
  if (client.connect(serverIP, 4000)) {
    Serial.print("Requesting: ");
    Serial.println(path);

    client.print("GET " + path + " HTTP/1.1\r\n");
    client.print("Host: ");
    client.print(serverIP);
    client.print(":4000\r\n");
    client.print("Connection: close\r\n\r\n");
    client.stop(); // fecha a conexão depois de enviar
  } else {
    Serial.println("Connection to server failed.");
  }
}

// Funções auxiliares
void printWifiData() {
  IPAddress ip = WiFi.localIP();
  Serial.print("IP Address: ");
  Serial.println(ip);

  byte mac[6];
  WiFi.macAddress(mac);
  Serial.print("MAC address: ");
  printMacAddress(mac);
}

void printCurrentNet() {
  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());

  byte bssid[6];
  WiFi.BSSID(bssid);
  Serial.print("BSSID: ");
  printMacAddress(bssid);

  long rssi = WiFi.RSSI();
  Serial.print("Signal strength (RSSI): ");
  Serial.println(rssi);

  byte encryption = WiFi.encryptionType();
  Serial.print("Encryption Type: ");
  Serial.println(encryption, HEX);
  Serial.println();
}

void printMacAddress(byte mac[]) {
  for (int i = 0; i < 6; i++) {
    if (i > 0) Serial.print(":");
    if (mac[i] < 16) Serial.print("0");
    Serial.print(mac[i], HEX);
  }
  Serial.println();
}
