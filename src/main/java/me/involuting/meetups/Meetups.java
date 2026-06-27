package me.involuting.meetups;

import lombok.Getter;
import lombok.Setter;
import me.involuting.meetups.arena.ArenaManager;
import me.involuting.meetups.arena.ArenaStorage;
import me.involuting.meetups.border.BorderManager;
import me.involuting.meetups.command.admin.MeetupCommand;
import me.involuting.meetups.command.admin.ScenarioCommand;
import me.involuting.meetups.command.admin.ToggleCommand;
import me.involuting.meetups.command.subcommand.*;
import me.involuting.meetups.game.countdown.GameCountdown;
import me.involuting.meetups.game.manager.GameManager;
import me.involuting.meetups.game.service.GameService;
import me.involuting.meetups.listener.BlockListener;
import me.involuting.meetups.listener.EntityListener;
import me.involuting.meetups.listener.InteractListener;
import me.involuting.meetups.listener.PlayerListener;
import me.involuting.meetups.player.PlayerManager;
import me.involuting.meetups.queue.QueueManager;
import me.involuting.meetups.scatter.ScatterManager;
import me.involuting.meetups.scoreboard.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
@Getter @Setter
public final class Meetups extends JavaPlugin {


    private ScatterManager scatterManager;
    private PlayerManager playerManager;
    private GameManager gameManager;
    private BorderManager borderManager;
    private ArenaManager arenaManager;
    private ArenaStorage arenaStorage;
    private GameService gameService;
    private QueueManager queueManager;
    private GameCountdown gameCountdown;
    private ScoreboardManager scoreboardManager;


    @Override
    public void onEnable() {

        registerManagers();
        registerServices();
        registerListeners();
        registerCommands();

        arenaStorage.reload();

        scoreboardManager.start();
    }

    @Override
    public void onDisable() {

        if (gameManager.hasGame()) {
            gameService.endGame(gameManager.getCurrentGame());
        }

        arenaManager.save();
    }

    private void registerManagers() {

        playerManager = new PlayerManager();
        scatterManager = new ScatterManager();
        gameManager = new GameManager();

        arenaStorage = new ArenaStorage(this);
        arenaManager = new ArenaManager(arenaStorage);

        borderManager = new BorderManager();
        scoreboardManager = new ScoreboardManager(this);
    }

    private void registerListeners(){
        Bukkit.getPluginManager().registerEvents(new BlockListener(gameManager), this);
        Bukkit.getPluginManager().registerEvents(new EntityListener(gameManager, playerManager), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(playerManager, gameManager, gameService, queueManager, scoreboardManager), this);
        Bukkit.getPluginManager().registerEvents(new InteractListener(queueManager), this);
    }

    private void registerServices() {

        gameService = new GameService(
                this,
                gameManager,
                borderManager,
                scatterManager,
                playerManager
        );

        queueManager = new QueueManager(
                this,
                gameService
        );


        gameService.setQueueManager(queueManager);
    }


    private void registerCommands(){
        MeetupCommand meetupCommand = new MeetupCommand(arenaStorage);

        meetupCommand.register(new CreateCommand(this, arenaManager, arenaStorage));
        meetupCommand.register(new DeleteCommand(this, arenaManager, arenaStorage));
        meetupCommand.register(new ListCommand(arenaManager, arenaStorage));
        meetupCommand.register(new InfoCommand(arenaManager, arenaStorage));

        meetupCommand.register(new SetWorldCommand(arenaManager, this, arenaStorage));
        meetupCommand.register(new SetLobbyCommand(arenaManager, this, arenaStorage));
        meetupCommand.register(new SetSpectatorCommand(arenaManager, this, arenaStorage));
        meetupCommand.register(new SetBorderCenterCommand(arenaManager, this, arenaStorage));

        meetupCommand.register(new SetBorderCommand(arenaManager,  this, arenaStorage));

        meetupCommand.register(new SetMinPlayersCommand(arenaManager, this, arenaStorage));
        meetupCommand.register(new SetMaxPlayersCommand(this, arenaManager, arenaStorage));

        meetupCommand.register(new SetCountdownCommand(arenaManager, this, arenaStorage));
        meetupCommand.register(new SetGracePeriodCommand(arenaManager, this, arenaStorage));
        meetupCommand.register(new SetBorderDelayCommand(arenaManager, this, arenaStorage));
        meetupCommand.register(new SetBorderShrinkTimeCommand(arenaManager, this));

        meetupCommand.register(new ToggleCommand(arenaManager, this));
        meetupCommand.register(new ScenarioCommand(arenaManager, this));
        meetupCommand.register(new StartGameCommand(arenaManager, gameManager, gameService, arenaStorage));
        meetupCommand.register(new JoinGameCommand(gameService, arenaManager));

        getCommand("meetups").setExecutor(meetupCommand);
    }


}
