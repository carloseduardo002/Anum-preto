package API;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LocalizerTest {

    private Localizer localizer;
    private HttpClient mockHttpClient;
    private HttpResponse<String> mockResponse;

    @BeforeEach
    public void setUp() {
        localizer = new Localizer() {}; // Instância anônima da classe Localizer
        localizer.setLocation("40.7128", "-74.0060"); // Definindo coordenadas para o teste

        mockHttpClient = Mockito.mock(HttpClient.class);
        mockResponse = Mockito.mock(HttpResponse.class);

        // Substituindo o HttpClient padrão pelo mock
        HttpClient.setHttpClient(mockHttpClient);
    }

    @Test
    public void testGetLocation_Success() throws Exception {
        // Configurando o comportamento do mock para retornar uma resposta bem-sucedida
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"daily\": {\"temperature_2m_max\": [30.0], \"temperature_2m_min\": [20.0]}}");

        Data result = localizer.getLocation();

        assertNotNull(result);
        assertEquals(30.0, result.getDaily().getTemperatureMax().get(0));
        assertEquals(20.0, result.getDaily().getTemperatureMin().get(0));
    }

    @Test
    public void testGetLocation_Failure() throws Exception {
        // Configurando o comportamento do mock para retornar uma resposta de erro
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        when(mockResponse.statusCode()).thenReturn(404); // Simulando um erro 404

        Data result = localizer.getLocation();

        assertNull(result);
    }

    @Test
    public void testSetLocation() {
        localizer.setLocation("34.0522", "-118.2437");

        assertEquals("34.0522", Localizer.latitude);
        assertEquals("-118.2437", Localizer.longitude);
    }

    @Test
    public void testGetData() {
        localizer.setLocation("40.7128", "-74.0060");

        // Simulando dados em data
        Data mockData = new Data();
        localizer.data = mockData; // Definindo um objeto mockado

        String result = localizer.getData();

        assertNotNull(result);
        assertEquals(mockData.toString(), result); // Verifica se o retorno é igual ao toString do mockData
    }
}