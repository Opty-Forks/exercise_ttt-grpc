package pt.tecnico.ttt.client;

import java.util.Scanner;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import pt.tecnico.ttt.contract.*;

public class TTTClient {

  /**
   * Set flag to true to print debug messages.
   * The flag can be set using the -Ddebug command line option.
   */
  private static final boolean DEBUG_FLAG = (System.getProperty("debug") != null);

  /** Helper method to print debug messages. */
  private static void debug(String debugMessage) {
    if (DEBUG_FLAG) System.err.println(debugMessage);
  }

  /** The main method is the starting point for the program. */
  public static void main(String[] args) {
    System.out.println(TTTClient.class.getSimpleName());

    // receive and print arguments
    System.out.printf("Received %d arguments%n", args.length);
    for (int i = 0; i < args.length; i++) {
      System.out.printf("arg[%d] = %s%n", i, args[i]);
    }

    // check arguments
    if (args.length < 2) {
      System.out.println("Argument(s) missing!");
      System.out.printf("Usage: java %s host port%n", TTTClient.class.getName());
      return;
    }

    final String host = args[0];
    final int port = Integer.parseInt(args[1]);
    final String target = host + ":" + port;
    debug("Target: " + target);

    // Channel is the abstraction to connect to a service endpoint.
    // Let us use plaintext communication because we do not have certificates.
    final ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

    // It is up to the client to determine whether to block the call.
    // Here we create a blocking stub, but an async stub, or an async stub with
    // Future are also available.
    TTTGrpc.TTTBlockingStub stub = TTTGrpc.newBlockingStub(channel);

    playGame(stub);
    channel.shutdown();
  }

  private static void playGame(TTTGrpc.TTTBlockingStub stub) {

    int player = 0; // Player number - 0 or 1
    int go; // Square selection number for turn
    int row; // Row index for a square
    int column; // Column index for a square
    int winner; // The winning player
    PlayResult play_res;

    /* Using try with scanner - ensures the resource is closed in the end, even if there are exceptions. */
    try (Scanner scanner = new Scanner(System.in)) {
      /* The main game loop. The game continues for up to 9 turns, as long as there is no winner. */
      do {
        /* Get valid player square selection. */
        do {
          do {
            /* Print current board. */
            debug("Calling currentBoard operation...");
            System.out.println(
                stub.currentBoard(CurrentBoardRequest.getDefaultInstance()).getBoard());

            System.out.printf(
                "\nPlayer %d, "
                    + "please enter the number of the square where you want to place your '%c' "
                    + "(or 10 to get the last play): ",
                player, (player == 1) ? 'X' : 'O');
            go = scanner.nextInt();
            debug("go = " + go);

            if (go == 10) {
              LastPlayResponse response = stub.lastPlay(LastPlayRequest.getDefaultInstance());
              System.out.printf(
                  "\nlast cell: %d; last player: %d\n",
                  response.getLastCell(), response.getLastPlayer());
            }
          } while (go < 1 || go > 9);

          /* Get row index of board. */
          row = --go / 3;
          /* Get column index of board. */
          column = go % 3;
          debug("row = " + row + ", column = " + column);

          PlayRequest request =
              PlayRequest.newBuilder().addPlay(row).addPlay(column).addPlay(player).build();
          play_res = stub.play(request).getPlayResult();

          if (play_res != PlayResult.SUCCESS) {
            displayResult(play_res);
          }
        } while (play_res != PlayResult.SUCCESS);

        /* Select next player. */
        player = (player + 1) % 2;

        winner = stub.checkWinner(CheckWinnerRequest.getDefaultInstance()).getCheckWinner();
      } while (winner == -1);

      /* Game is over so display the final board. */
      System.out.println(stub.currentBoard(CurrentBoardRequest.getDefaultInstance()).getBoard());

      /* Display result message. */
      if (winner == 2) System.out.println("\nHow boring, it is a draw");
      else System.out.println("\nCongratulations, player " + winner + ", YOU ARE THE WINNER!");
    }
  }

  private static void displayResult(PlayResult play_res) {
    switch (play_res) {
      case OUT_OF_BOUNDS:
        System.out.print("Position outside board.");
        break;
      case SQUARE_TAKEN:
        System.out.print("Square already taken.");
        break;
      case WRONG_TURN:
        System.out.print("Not your turn.");
        break;
      case GAME_FINISHED:
        System.out.print("Game has finished.");
        break;
      default:
        System.out.println("Unexpected result: " + play_res);
        break;
    }
    System.out.println("Try again...");
  }
}
