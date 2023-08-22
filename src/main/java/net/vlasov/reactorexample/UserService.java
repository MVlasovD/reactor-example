package net.vlasov.reactorexample;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.lang.Thread.sleep;

public class UserService {

    public static void main(String[] args) {
        String urlString = "http://localhost:8088/hello";

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Устанавливаем метод запроса
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            System.out.println("Отправлен запрос: GET " + urlString);
            System.out.println("Код ответа: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                System.out.println("Ответ от сервера:");
                System.out.println(response.toString());
            } else {
                System.out.println("Запрос завершился неудачно.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
