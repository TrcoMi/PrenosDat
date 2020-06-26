package sk.fri.uniza;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import sk.fri.uniza.api.WeatherStationService;
import sk.fri.uniza.model.WeatherData;

import java.io.IOException;
import java.util.List;

public class IotNode {
    private final Retrofit retrofit;
    private final WeatherStationService weatherStationService;

    public IotNode() {

        retrofit = new Retrofit.Builder()
                // Url adresa kde je umietnená WeatherStation služba
                // http://ip172-18-0-108-br7sr1qosm4g00c72eu0-9000.direct.labs.play-with-docker.com/
                // http://ip172-18-0-60-brn04hboudsg00akk8l0-9000.direct.labs.play-with-docker.com/
                .baseUrl("http://ip172-18-0-28-brqvrnroudsg00fv1fl0-9000.direct.labs.play-with-docker.com/:9000/")
                // Na konvertovanie JSON objektu na java POJO použijeme
                // Jackson knižnicu
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        // Vytvorenie inštancie komunikačného rozhrania
        weatherStationService = retrofit.create(WeatherStationService.class);

    }

    public WeatherStationService getWeatherStationService() {
        return weatherStationService;
    }

    public double getAverageTemperature(String station, String from, String to) {
        Call<List<WeatherData>>  historyWeather = getWeatherStationService().getHistoryWeather(station, from, to);
        double averageTemp = 0;

        try {
            // Odoslanie požiadavky na server pomocou REST rozhranie
            Response<List<WeatherData>> response = historyWeather.execute();

            if (response.isSuccessful()) { // Dotaz na server bol neúspešný
                //Získanie údajov vo forme inštancie triedy WeatherData
                List<WeatherData> body = response.body();
                for (WeatherData i : body) {
                    averageTemp = averageTemp + i.getAirTemperature();
                    //System.out.println(i.getAirTemperature());
                }
                averageTemp = averageTemp / body.size();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return  averageTemp;
    }
}
