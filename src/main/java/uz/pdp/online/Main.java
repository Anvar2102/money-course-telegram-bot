package uz.pdp.online;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import uz.pdp.online.models.CurrencyBank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Scanner;

public class Main extends TelegramLongPollingBot {

    static String firstCurrency = "";
    static String secondCurrency = "";
    private static ArrayList<CurrencyBank> currencies = new ArrayList<>();

    public static void main(String[] args) {

        TelegramBotsApi api = null;
        try {
            api = new TelegramBotsApi(DefaultBotSession.class);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        try {
            api.registerBot((LongPollingBot) new Main());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }


    }

    @Override
    public String getBotUsername() {
        return "Abbosxon_Currency_bot";
    }

    @Override
    public String getBotToken() {
        return "5172481658:AAGGPdUrOwOban2h1V2IMlD5pGRvpgucCNM";
    }


    @Override
    public void onUpdateReceived(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        String input = update.getMessage().getText();


        if (input.toLowerCase().contains("start")) {
            firstCurrency = "";
            secondCurrency = "";
            sendMessage.setText("Choose first currency:\nBirinchi valyutani tanlang:");
            startingAction(sendMessage);
        } else if (input.contains("SUM") || input.contains("USD") || input.contains("EUR") || input.contains("CNY")) {
            if (firstCurrency.equals("")) {
                firstCurrency = input;
                sendMessage.setText("Choose second currency:\nIkkinchi valyutani tanlang:");
                startingAction(sendMessage);
            } else {

                secondCurrency = input;
                sendMessage.setText("Input any amount of money at your chose currency:\nBirinchi tanlangan valyutada pul miqdorini kiriting:");
            }

        } else if (Double.valueOf(input) > 0) {
            sendMessage.setText("Transfering value is " + String.valueOf(currencyTranfering(sendMessage, Double.valueOf(input))) + " " + secondCurrency);
        } else {
            sendMessage.setText("I'm sorry! I didn't understand you!!!");
        }


        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void startingAction(SendMessage sendMessage) {

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(keyboardMarkup);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setSelective(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardButton button1 = new KeyboardButton("Start");
        row1.add(button1);
        keyboardRows.add(row1);


        KeyboardRow row2 = new KeyboardRow();
        KeyboardButton button2 = new KeyboardButton("SUM(Sum)");
        row2.add(button2);

        KeyboardButton button3 = new KeyboardButton("USD(Dollar)");
        row2.add(button3);
        keyboardRows.add(row2);


        KeyboardRow row3 = new KeyboardRow();
        KeyboardButton button4 = new KeyboardButton("EUR(Euro)");
        row3.add(button4);

        KeyboardButton button5 = new KeyboardButton("CNY(Yuan)");
        row3.add(button5);
        keyboardRows.add(row3);

        keyboardMarkup.setKeyboard(keyboardRows);
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static double currencyTranfering(SendMessage sendMessage, double input) {

        if (firstCurrency.equals(secondCurrency)) {
            return Double.valueOf(input);
        }
        URLConnection urlConnection = null;
        try {
            URL url = new URL("https://cbu.uz/oz/arkhiv-kursov-valyut/json/");
            urlConnection = url.openConnection();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Type type = new TypeToken<List<Currency>>() {
        }.getType();
        if (urlConnection == null) throw new AssertionError();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();

        CurrencyBank[] currencies = gson.fromJson(reader, CurrencyBank[].class);
        if (urlConnection == null) throw new AssertionError();

        double result =0;
        double firstRate = 0;
        double secondRate = 0;
        for (CurrencyBank currency : currencies) {
            if (firstCurrency.contains(currency.getCcy())) {
                firstRate = currency.getRate();
            }
            if (secondCurrency.contains(currency.getCcy())) {
                secondRate = currency.getRate();
            }
        }

        if(secondCurrency.contains("SUM")){
            result=input*firstRate;
        }else
        if (firstCurrency.contains("SUM")){
            result=input/secondRate;
        }else {
            result=(input*firstRate)/secondRate;
        }
        return result;
    }

}
