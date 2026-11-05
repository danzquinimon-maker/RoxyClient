
import { world, system } from "@minecraft/server";

const ROXY_VERSION = "1.0.1";
const PREFIX = "§l§5[ROXY]§r ";

class PlayerTracker {
    constructor() {
        this.players = new Map();
    }

    update(player) {
        const health = player.getComponent("minecraft:health");

        this.players.set(player.id, {
            name: player.name,
            location: { ...player.location },
            dimension: player.dimension.id,
            health: health?.currentValue ?? 0
        });
    }

    remove(id) {
        this.players.delete(id);
    }

    getAll() {
        return [...this.players.values()];
    }
}

const tracker = new PlayerTracker();

function getDirection(yaw) {
    const directions = [
        "S", "SW", "W", "NW",
        "N", "NE", "E", "SE"
    ];

    const normalized = ((yaw % 360) + 360) % 360;

    return directions[Math.round(normalized / 45) % 8];
}

function showHelp(player) {
    player.sendMessage(
        `${PREFIX}§dROXY CLIENT v${ROXY_VERSION}\n\n` +
        "§bCommands:\n" +
        "!roxy help - Show commands\n" +
        "!roxy coords - Show coordinates\n" +
        "!roxy players - Show online players\n" +
        "!roxy status - Show player status"
    );
}

function showCoordinates(player) {
    const loc = player.location;
    const rotation = player.getRotation();

    player.sendMessage(
        `${PREFIX}§dCOORDINATES\n` +
        `§6X: §f${Math.floor(loc.x)}\n` +
        `§6Y: §f${Math.floor(loc.y)}\n` +
        `§6Z: §f${Math.floor(loc.z)}\n` +
        `§6Direction: §f${getDirection(rotation.y)}\n` +
        `§6Dimension: §f${player.dimension.id}`
    );
}

function showPlayers(player) {
    const players = tracker.getAll();

    let output =
        `${PREFIX}§dPLAYERS ONLINE (${players.length})\n`;

    players.forEach((p, index) => {
        output +=
            `§f${index + 1}. §a${p.name} §7- ` +
            `§c${p.health.toFixed(1)} HP\n`;
    });

    player.sendMessage(output);
}

function showStatus(player) {
    const health = player.getComponent("minecraft:health");

    if (!health) {
        player.sendMessage(`${PREFIX}Health unavailable.`);
        return;
    }

    player.sendMessage(
        `${PREFIX}§dPLAYER STATUS\n` +
        `§6Name: §f${player.name}\n` +
        `§6Health: §c${health.currentValue.toFixed(1)}` +
        `/${health.effectiveMax.toFixed(1)}`
    );
}

function handleCommand(player, message) {
    const args = message.trim().split(/\s+/);
    const command = args[1]?.toLowerCase() ?? "help";

    switch (command) {
        case "help":
            showHelp(player);
            break;

        case "coords":
            showCoordinates(player);
            break;

        case "players":
            showPlayers(player);
            break;

        case "status":
            showStatus(player);
            break;

        default:
            player.sendMessage(
                `${PREFIX}Unknown command. Use !roxy help`
            );
    }
}

// Player joins and respawns
world.afterEvents.playerSpawn.subscribe((event) => {
    const player = event.player;

    tracker.update(player);

    if (event.initialSpawn) {
        player.sendMessage(
            `${PREFIX}Welcome to ROXY CLIENT v${ROXY_VERSION}!`
        );
    }
});

// Player leaves
world.afterEvents.playerLeave.subscribe((event) => {
    tracker.remove(event.playerId);
});

// Chat command handling
world.beforeEvents.chatSend.subscribe((event) => {
    const message = event.message.trim();

    if (!/^!roxy(?:\s|$)/i.test(message)) {
        return;
    }

    event.cancel = true;

    const player = event.sender;

    system.run(() => {
        try {
            handleCommand(player, message);
        } catch (error) {
            console.warn(`${PREFIX}Command error: ${error}`);
            player.sendMessage(
                `${PREFIX}An error occurred while running that command.`
            );
        }
    });
});

// Update player information once per second
system.runInterval(() => {
    for (const player of world.getAllPlayers()) {
        tracker.update(player);
    }
}, 20);

console.warn(
    `${PREFIX}ROXY CLIENT v${ROXY_VERSION} loaded successfully!`
);
