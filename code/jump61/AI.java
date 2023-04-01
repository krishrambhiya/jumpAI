
package jump61;

import java.util.Random;

import static jump61.Side.*;

/** An automated Player.
 *  @author Krish Rambhiya
 */
class AI extends Player {

    /** A new player of GAME initially COLOR that chooses moves automatically.
     *  SEED provides a random-number seed used for choosing moves.
     */
    AI(Game game, Side color, long seed) {
        super(game, color);
        _random = new Random(seed);
    }

    @Override
    String getMove() {
        Board board = getGame().getBoard();

        assert getSide() == board.whoseMove();
        int choice = searchForMove();
        getGame().reportMove(board.row(choice), board.col(choice));
        return String.format("%d %d", board.row(choice), board.col(choice));
    }

    /** Return a move after searching the game tree to DEPTH>0 moves
     *  from the current position. Assumes the game is not over. */
    private int searchForMove() {
        Board work = new Board(getBoard());
        int value;
        assert getSide() == work.whoseMove();
        _foundMove = -1;
        if (getSide() == RED) {
            value = minMax(work, 4, true, 1,
                    -Integer.MAX_VALUE, Integer.MAX_VALUE);
        } else {
            value = minMax(work, 4, true, -1,
                    -Integer.MAX_VALUE, Integer.MAX_VALUE);
        }
        return _foundMove;
    }


    /** Find a move from position BOARD and return its value, recording
     *  the move found in _foundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _foundMove. If the game is over
     *  on BOARD, does not set _foundMove. */
    private int minMax(Board board, int depth, boolean saveMove,
                       int sense, int alpha, int beta) {
        if (depth == 0 || board.getWinner() != null) {
            return staticEval(board, Integer.MAX_VALUE);
        }
        if (sense == -1) {
            int minimumVal = Integer.MAX_VALUE;
            int bestCurrentMove = -1;
            for (int i = 0; i < board.size() * board.size(); i++) {
                if (board.isLegal(BLUE, i)) {
                    board.addSpot(BLUE, i);
                    int currScore = minMax(board, depth - 1, false,
                            1, alpha, beta);
                    if (currScore <= minimumVal) {
                        minimumVal = currScore;
                        bestCurrentMove = i;
                    }
                    board.undo();
                }
                if (alpha >= beta) {
                    return minimumVal;
                }
            }
            if (saveMove) {
                _foundMove = bestCurrentMove;
            }
            return minimumVal;
        } else {
            int maximumVal = Integer.MIN_VALUE;
            int bestCurrentMove = -1;
            for (int i = 0; i < board.size() * board.size(); i++) {
                if (board.isLegal(RED, i)) {
                    board.addSpot(RED, i);
                    int currScore = minMax(board, depth - 1, false,
                            -1, alpha, beta);
                    if (currScore >= maximumVal) {
                        maximumVal = currScore;
                        bestCurrentMove = i;
                    }
                    board.undo();
                }
                if (alpha >= beta) {
                    return maximumVal;
                }
            }
            if (saveMove) {
                _foundMove = bestCurrentMove;
            }
            return maximumVal;
        }
    }

    /** Return a heuristic estimate of the value of board position B.
     *  Use WINNINGVALUE to indicate a win for Red and -WINNINGVALUE to
     *  indicate a win for Blue. */
    private int staticEval(Board b, int winningValue) {
        if (b.getWinner() != null) {
            if (b.getWinner() == RED) {
                return winningValue;
            } else if (b.getWinner() == BLUE) {
                return -winningValue;
            }
        }
        return b.numOfSide(RED) - b.numOfSide(BLUE);
    }

    /** A random-number generator used for move selection. */
    private Random _random;

    /** Used to convey moves discovered by minMax. */
    private int _foundMove;
}

