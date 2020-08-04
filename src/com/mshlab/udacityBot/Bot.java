package com.mshlab.udacityBot;

import com.mshlab.udacityBot.udacityapi.*;
import com.mshlab.udacityBot.udacityapi.model.*;
import com.mshlab.udacityBot.uitils.Helper;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Consumer;

import static com.mshlab.udacityBot.Consts.*;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.CREATOR;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

public class Bot extends AbilityBot {

    protected Bot(String botToken, String botUsername) {
        super(botToken, botUsername);
    }


    /**
     * Use the database to fetch a count per user and increments.
     * <p>
     * Use /count to experiment with this ability.
     */
    public Ability login() {
        return Ability.builder()
                .name("login")
                .input(2)
                .info("login to Mentorship Dashboard")
                .privacy(PUBLIC)
                .locality(ALL)
                .input(0)
                .action(new Consumer<MessageContext>() {
                    @Override
                    public void accept(MessageContext ctx) {
                        sendTyping(ctx.chatId());
                        // db.getMap takes in a string, this must be unique and the same everytime you want to call the exact same map
                        // TODO: Using integer as a key in this db map is not recommended, it won't be serialized/deserialized properly if you ever decide to recover/backup db
                        String userId = ctx.user().getId().toString();
                        Map<String, String> tokenMap = db.getMap("tokens");
                        // Get and increment counter, put it back in the map
                        if (ctx.arguments().length != 2) {
                            String message = "please login with correct format message /login yourEmail yourPassword";
                            silent.send(message, ctx.chatId());

                        } else {
                            String token = getToken(ctx.user().getId());
                            if (token != null) {
                                String message = String.format("you're already logged in, to reset your token send /reset", ctx.firstArg());
                                silent.send(message, ctx.chatId());
                                silent.send("you can explore dashboard commands by using /commands", ctx.chatId());
                            } else {
                                token = new UdacityAPI().login(ctx.firstArg(), ctx.secondArg());
                                if (token != null) {
                                    tokenMap.put(userId, token);
                                    String message = String.format("logged in successfully with account %s", ctx.firstArg());
                                    silent.send(message, ctx.chatId());
                                    silent.send("you can explore dashboard commands by using /commands", ctx.chatId());
                                } else {
                                    String message = String.format("cannot login to %s . invalid credentials", ctx.firstArg());
                                    silent.send(message, ctx.chatId());
                                }
                            }
                        }

                    }
                })
                .build();
    }


    public Ability start() {
        return Ability
                .builder()
                .name("start")
                .info("start Udacity Bot to control Mentorship Dashboard")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(new Consumer<MessageContext>() {
                    @Override
                    public void accept(MessageContext ctx) {
                        sendTyping(ctx.chatId());
                        String message = "";
                        if (getToken(ctx.user().getId()) != null) {
                            String.format("Hello %s you're already logged in.\n you can use /reset to refresh your token", ctx.user().getFirstName());
                        } else {
                            message = String.format("Hello %s please login to .\n use /login yourEmail YourPassword", ctx.user().getFirstName());
                        }
                        silent.send(message, ctx.chatId());

                    }
                })
                .build();
    }


    public Ability reset() {
        return Ability
                .builder()
                .name("reset")
                .info("remove the token and clear the bot to start again")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(new Consumer<MessageContext>() {
                    @Override
                    public void accept(MessageContext ctx) {
                        sendTyping(ctx.chatId());
                        db.getMap("tokens").remove(ctx.user().getId());
                        if (getToken(ctx.user().getId()) == null) {
                            String message = "token remove successfully to login again.\n use /login yourEmail YourPassword";
                            silent.send(message, ctx.chatId());
                        }
                    }
                })
                .build();
    }

    public Ability clean() {
        return Ability
                .builder()
                .name("clean")
                .info("clean the database")
                .locality(ALL)
                .privacy(CREATOR)
                .action(new Consumer<MessageContext>() {
                    @Override
                    public void accept(MessageContext ctx) {
                        sendTyping(ctx.chatId());
                        db.clear();
                        String message = "database cleaned successfully";
                        silent.send(message, ctx.chatId());
                    }
                }).build();

    }

    public Ability assignedProjects() {
        return Ability
                .builder()
                .name("assignedprojects")
                .info("check if you have any projects wait for review")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(new Consumer<MessageContext>() {
                    @Override
                    public void accept(MessageContext ctx) {
                        sendTyping(ctx.chatId());
                        String token = getToken(ctx.user().getId());
                        if (token != null) {
                            try {
                                Submission[] submissionRequest = new UdacityAPI(token).getAssignedSubmissions();
                                String message = "";

                                if (submissionRequest.length > 0) {

                                    message = "you have " + submissionRequest.length + " wait for review:\n";
                                    for (Submission submission : submissionRequest) {
                                        // TODO: 20/06/2020 use below date
                                        String correctDate = Helper.convertDateGMT(submission.getAssignedAt());
                                        message += "-" + submission.getProject().getName() + "\n assigned at " + correctDate + "\n";
                                    }
                                } else {
                                    message = "no projects to review.\n check your queue status /queuestatus";
                                }

                                silent.send(message, ctx.user().getId());
                            } catch (IOException | UdacityException e) {
                                silent.send(e.getMessage(), ctx.user().getId());
                            } catch (ParseException e) {
                                e.printStackTrace();
                                silent.send("error in the code.\n*please contact @mshlab about it", ctx.user().getId());
                            }

                        } else {
                            notokenMessage(ctx.user().getId());
                        }
                    }
                }).build();

    }



    public Ability joinqueue() {
        return Ability
                .builder()
                .name("joinqueue")
                .info("Join Queue to queue to review all your projects")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(new Consumer<MessageContext>() {
                    @Override
                    public void accept(MessageContext ctx) {
                        sendTyping(ctx.chatId());
                        String token = getToken(ctx.user().getId());
                        if (token != null) {
                            try {
                                SubmissionRequest submissionRequest = new UdacityAPI(token).createSubmissionRequestAllProjects();
                                String message = "joined the queue successfully, udacity will inform you for new awaiting tasks";
                                silent.send(message, ctx.user().getId());
                            } catch (IOException | UdacityException e) {
                                if (e.getMessage().contains("Reached reviewer limit")) {
                                    silent.send("you are already in queue.\ncheck your status /queuestatus", ctx.user().getId());
                                } else {
                                    silent.send(e.getMessage(), ctx.user().getId());
                                }
                            }

                        } else {
                            notokenMessage(ctx.user().getId());
                        }
                    }
                }).build();

    }


    public Ability queueStatus() {
        return Ability
                .builder()
                .name("queuestatus")
                .info("get cureens Queue status")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(new Consumer<MessageContext>() {
                    @Override
                    public void accept(MessageContext ctx) {
                        sendTyping(ctx.chatId());
                        String token = getToken(ctx.user().getId());
                        if (token != null) {
                            try {
                                SubmissionRequest[] submissionRequests = new UdacityAPI(token).getSubmissionsRequests();
                                String finalmessage;
                                if (submissionRequests.length > 0) {
                                    String correctDate = Helper.convertDateGMT(submissionRequests[0].getClosedAt());

                                    finalmessage = "you're in queue until\n" + correctDate;
                                } else {
                                    finalmessage = "you are not in any queue.\n*join the queue by send /joinqueue";
                                }
                                silent.send(finalmessage, ctx.user().getId());

                            } catch (IOException | UdacityException e) {
                                silent.send(e.getMessage(), ctx.user().getId());
                            } catch (ParseException e) {
                                e.printStackTrace();
                                silent.send("error in the code.\n*please contact @mshlab about it", ctx.user().getId());
                            }

                        } else {
                            notokenMessage(ctx.user().getId());
                        }
                    }
                }).build();

    }


    public Ability refresh() {
        return Ability
                .builder()
                .name("refresh")
                .info("refresh queue to review all your projects")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(new Consumer<MessageContext>() {
                    @Override
                    public void accept(MessageContext ctx) {

                        sendTyping(ctx.chatId());
                        String token = getToken(ctx.user().getId());
                        if (token != null) {
                            try {
                                new UdacityAPI(token).deleteAllSubmissionRequest();
                                SubmissionRequest submissionRequest = new UdacityAPI(token).createSubmissionRequestAllProjects();
//                                ArrayList<SubmissionRequest> submissionRequest = new UdacityAPI(token).refreshAllSubmissionRequest(); //// TODO: 20/06/2020 not working, need to fix
                                String message = "queue Refreshed successfully, udacity will inform you for new awaiting tasks.";
                                silent.send(message, ctx.user().getId());
                            } catch (IOException | UdacityException e) {
                                silent.send(e.getMessage(), ctx.user().getId());
                            }

                        } else {
                            notokenMessage(ctx.user().getId());
                        }
                    }
                }).build();

    }


    public Ability exitQueue() {
        return Ability
                .builder()
                .name("exitqueue")
                .info("exit from queue to review all your projects")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(new Consumer<MessageContext>() {
                    @Override
                    public void accept(MessageContext ctx) {

                        sendTyping(ctx.chatId());
                        String token = getToken(ctx.user().getId());
                        if (token != null) {
                            try {
                                new UdacityAPI(token).deleteAllSubmissionRequest();
                                String message = "exit from the queue successfully.";
                                silent.send(message, ctx.user().getId());
                            } catch (IOException | UdacityException e) {
                                silent.send(e.getMessage(), ctx.user().getId());
                            }

                        } else {
                            notokenMessage(ctx.user().getId());
                        }
                    }
                }).build();

    }

    public Ability earning() {
        return Ability
                .builder()
                .name("earning")
                .info("get your total earning since specific month.\n required date like 09/2019")
                .locality(ALL)
                .input(1)
                .privacy(PUBLIC)
                .action(new Consumer<MessageContext>() {
                    private String correctDate;

                    @Override
                    public void accept(MessageContext ctx) {
                        sendTyping(ctx.chatId());
                        if (ctx.firstArg() != null) {
                            try {
                                DateFormat formatter = new SimpleDateFormat(DEFAULT_PATTERN);
                                Date userFormattedDate = formatter.parse(ctx.firstArg());
                                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                                correctDate = dateFormat.format(userFormattedDate);
                            } catch (ParseException e) {
                                silent.send("error date format , try again", ctx.user().getId());
                            }

                        }

                        try {
                            String token = getToken(ctx.user().getId());
                            if (token != null) {
                                String totalEarning = new UdacityAPI(token).getTotalEarning(correctDate);
                                String message = String.format("your total earning is %s since %s\n*this total without Questions", totalEarning, ctx.firstArg());
                                silent.send(message, ctx.user().getId());
                            } else {
                                notokenMessage(ctx.user().getId());
                            }
                        } catch (IOException | UdacityException e) {
                            silent.send(e.getMessage(), ctx.user().getId());
                        }

                    }
                })
                .build();
    }

    private void notokenMessage(Integer id) {
        silent.send("no token found, please login using /login email password", id);
    }


    private void sendTyping(long Chatid) {
        SendChatAction sendChatAction = new SendChatAction();
        sendChatAction.setAction(ActionType.TYPING);
        sendChatAction.setChatId(Chatid);
        silent.execute(sendChatAction);
    }

    private String getToken(int userId) {
        Map<String, String> countMap = db.getMap("tokens");
        return countMap.compute(String.valueOf(userId), (id, token) -> token);
    }

    @Override
    public int creatorId() {
        return 826108395;
    }


}
