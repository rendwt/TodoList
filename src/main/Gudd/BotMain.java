public class BotMain {
    public static void main( String[] args )
    {

        try{
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bot());
        }
        catch(Exception e){
            e.printStackTrace();
        }
}
}
