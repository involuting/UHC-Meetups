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
import me.involuting.meetups.game.manager.GameManager;
import me.involuting.meetups.game.service.GameService;
import me.involuting.meetups.listener.BlockListener;
import me.involuting.meetups.listener.EntityListener;
import me.involuting.meetups.listener.PlayerListener;
import me.involuting.meetups.player.PlayerManager;
import me.involuting.meetups.scatter.ScatterManager;
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


    @Override
    public void onEnable() {
        registerManagers();
        registerCommands();
        registerListeners();


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerManagers() {


        playerManager = new PlayerManager();
        scatterManager = new ScatterManager();


        gameManager = new GameManager();


        arenaManager = new ArenaManager(arenaStorage);

        arenaStorage = new ArenaStorage(this, arenaManager);
        arenaStorage.load();


        if (gameManager.getGame() == null) {

            borderManager = null;
        } else {
            borderManager = new BorderManager(gameManager.getGame());
        }


        gameService = new GameService(this, gameManager, borderManager, scatterManager);
    }

    private void registerListeners(){
        Bukkit.getPluginManager().registerEvents(new BlockListener(gameManager), this);
        Bukkit.getPluginManager().registerEvents(new EntityListener(gameManager, playerManager), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(playerManager, gameManager, gameService), this);
    }


    private void registerCommands(){
        MeetupCommand meetupCommand = new MeetupCommand();

        meetupCommand.register(new CreateCommand(this, arenaManager));
        meetupCommand.register(new DeleteCommand(this, arenaManager));
        meetupCommand.register(new ListCommand(arenaManager));
        meetupCommand.register(new InfoCommand(arenaManager));

        meetupCommand.register(new SetWorldCommand(arenaManager, this));
        meetupCommand.register(new SetLobbyCommand(arenaManager, this));
        meetupCommand.register(new SetSpectatorCommand(arenaManager, this));
        meetupCommand.register(new SetBorderCenterCommand(arenaManager, this));

        meetupCommand.register(new SetBorderCommand(arenaManager, this));

        meetupCommand.register(new SetMinPlayersCommand(arenaManager, this));
        meetupCommand.register(new SetMaxPlayersCommand(this, arenaManager));

        meetupCommand.register(new SetCountdownCommand(arenaManager, this));
        meetupCommand.register(new SetGracePeriodCommand(arenaManager, this));
        meetupCommand.register(new SetBorderDelayCommand(arenaManager, this));
        meetupCommand.register(new SetBorderShrinkTimeCommand(arenaManager, this));

        meetupCommand.register(new ToggleCommand(arenaManager, this));
        meetupCommand.register(new ScenarioCommand(arenaManager, this));
        meetupCommand.register(new StartGameCommand(arenaManager, gameManager, gameService));

        getCommand("meetup").setExecutor(meetupCommand);
    }


}
