package se.anyro.userinfobot;

import static se.anyro.userinfobot.BuildVars.OWNER;
import static se.anyro.userinfobot.BuildVars.TOKEN;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.anyro.tgbotapi.TgBotApi;
import se.anyro.tgbotapi.TgBotApi.ErrorListener;
import se.anyro.tgbotapi.types.Message;
import se.anyro.tgbotapi.types.Update;
import se.anyro.tgbotapi.types.User;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@SuppressWarnings("serial")
public class UserInfoServlet extends HttpServlet implements ErrorListener {

    private TgBotApi api;
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public UserInfoServlet() {
        super();
        api = new TgBotApi(TOKEN, OWNER, this);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setStatus(200);

		try {
            Update update = api.parseFromWebhook(req.getReader());
            if (!update.isMessage()) {
                return;
            }
            Message message = update.message;
            User forwardFrom = message.forward_from;
            User user = forwardFrom;
            if (forwardFrom == null) {
                // Not forwarded so show info of the sender instead
                user = message.from;
            }
            StringBuilder builder = new StringBuilder();
            if (user.username != null) {
                builder.append("@").append(user.username).append('\n');
            }
            builder.append("Id: ").append(user.id).append('\n');
            builder.append("First: ").append(user.first_name).append('\n');
            if (user.last_name != null) {
                builder.append("Last: ").append(user.last_name).append('\n');
            }
            // Separate message for easy copy/paste on mobile
            if (message.from.id == OWNER && forwardFrom != null) {
                api.sendMessage(OWNER, String.valueOf(user.id));
            }
            api.sendMessage(message.from.id, builder.toString());
        } catch (Exception e) {
            api.debug(e);
        }
    }

    @Override
    public void onError(int errorCode, String description) {
        // Ignore blocked users
        if (errorCode == 403) {
            return;
        }
        api.debug(new Exception("ErrorCode " + errorCode + ", " + description));
    }
}