package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.PlayerTurn
import com.aigamelabs.swduel.players.KeyboardPlayer
import com.aigamelabs.swduel.players.MctsHighestScore
import com.aigamelabs.swduel.players.RandomPlayer
import com.aigamelabs.utils.RandomWithTracker
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.apache.commons.cli.DefaultParser

class Main {
    companion object {

        @JvmStatic
        private fun buildArgParser(): Options {
            val optionP1 = Option.builder("P1")
                    .required(true)
                    .hasArg()
                    .desc("Controller for Player 1 (one of MCTS, Keyboard, Random)")
                    .longOpt("player1")
                    .build()
            val optionP2 = Option.builder("P2")
                    .required(true)
                    .hasArg()
                    .desc("Controller for Player 2 (one of MCTS, Keyboard, Random)")
                    .longOpt("player2")
                    .build()
            val optionLogs = Option.builder("L")
                    .required(true)
                    .hasArg()
                    .desc("The S option")
                    .longOpt("logs-folder")
                    .build()
            val optionInit = Option.builder("S")
                    .hasArg()
                    .desc("Location of JSON file containing the initial state")
                    .longOpt("initial-state")
                    .build()
            val options = Options()
            options.addOption(optionP1)
            options.addOption(optionP2)
            options.addOption(optionLogs)
            options.addOption(optionInit)
            return options
        }

        @JvmStatic
        fun main(args: Array<String>) {
            val options = buildArgParser()
            val parser = DefaultParser()
            val commandLine = parser.parse(options, args)

            val logsLocation = commandLine.getOptionValue("L")
            val player1Controller = commandLine.getOptionValue("P1")
            val player2Controller = commandLine.getOptionValue("P2")
            val initGameStateLocation = commandLine.getOptionValue("S")

            val generator = RandomWithTracker(Random().nextLong(), true)
            val initGameState = if (commandLine.hasOption("S")) {
                val content = readFile(initGameStateLocation, Charset.defaultCharset())
                GameState.loadFromJson(JSONObject(content))
            }
            else
                GameStateFactory.createNewGameState(generator)


            val gameId = SimpleDateFormat("yyyy-MM-dd HH.mm.ss").format(Calendar.getInstance().time)
            val gameData = GameData(player1Controller, player2Controller)
            val player1 = Pair(PlayerTurn.PLAYER_1, getPlayer(player1Controller, gameData, gameId, logsLocation))
            val player2 = Pair(PlayerTurn.PLAYER_2, getPlayer(player2Controller, gameData, gameId, logsLocation))
            val game = Game(gameId, mapOf(player1, player2), logsLocation)

            generator.popAll()
            game.mainLoop(initGameState, generator)
            System.exit(0)
        }

        @Throws(IOException::class)
        // From https://stackoverflow.com/a/326440
        private fun readFile(path: String, encoding: Charset): String {
            val encoded = Files.readAllBytes(Paths.get(path))
            return String(encoded, encoding)
        }

        private fun getPlayer(playerClass: String, gameData: GameData, gameId: String, logsPath: String): Player {
            return when (playerClass) {
                "MCTS" -> MctsHighestScore(PlayerTurn.PLAYER_1, "MCTS(HS)", gameId, gameData, logsPath)
                "Random" -> RandomPlayer("Random", gameData)
                "Keyboard" -> KeyboardPlayer("Keyboard", gameData)
                else -> throw Exception("Unknown player controller " + playerClass)
            }
        }
    }
}