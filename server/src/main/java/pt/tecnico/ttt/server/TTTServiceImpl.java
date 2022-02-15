package pt.tecnico.ttt.server;

import io.grpc.stub.StreamObserver;

import pt.tecnico.ttt.contract.*;

public class TTTServiceImpl extends TTTGrpc.TTTImplBase {

  /** Game implementation. */
  private final TTTGame ttt = new TTTGame();

  @Override
  public void currentBoard(
      CurrentBoardRequest request, StreamObserver<CurrentBoardResponse> responseObserver) {
    // StreamObserver is used to represent the gRPC stream between the server and
    // client in order to send the appropriate responses (or errors, if any occur).

    CurrentBoardResponse response =
        CurrentBoardResponse.newBuilder().setBoard(ttt.toString()).build();

    // Send a single response through the stream.
    responseObserver.onNext(response);
    // Notify the client that the operation has been completed.
    responseObserver.onCompleted();
  }

  @Override
  public void play(PlayRequest request, StreamObserver<PlayResponse> responseObserver) {

    int row = request.getPlay(0);
    int column = request.getPlay(1);
    int player = request.getPlay(2);

    PlayResponse response =
        PlayResponse.newBuilder().setPlayResult(ttt.play(row, column, player)).build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void checkWinner(
      CheckWinnerRequest request, StreamObserver<CheckWinnerResponse> responseObserver) {

    CheckWinnerResponse response =
        CheckWinnerResponse.newBuilder().setCheckWinner(ttt.checkWinner()).build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void lastPlay(LastPlayRequest request, StreamObserver<LastPlayResponse> responseObserver) {

    LastPlayResponse response =
        LastPlayResponse.newBuilder()
            .setLastCell(ttt.getLastCell())
            .setLastPlayer(ttt.getLastPlayer())
            .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
