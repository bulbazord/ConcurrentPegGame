package main

import (
	"fmt"
	"os"
	"strconv"
)

/*
 * Stack stuff!
 * Implemented using slices!
 */
type stack [][]int

func NewStack() stack {
	return make([][]int, 0)
}

func (s stack) IsEmpty() bool {
	return len(s) == 0
}

func (s *stack) Push(move []int) {
	(*s) = append((*s), move)
}

func (s stack) Length() int {
	return len(s)
}

func (s *stack) Pop() ([]int, bool) {
	if len(*s) == 0 {
		return nil, false
	}
	d := (*s)[len(*s) - 1]
	(*s) = (*s)[:len(*s) - 1]
	return d, true
}

func (s stack) Clone() stack {
	same := make([][]int, len(s))
	copy(same, s)
	return same
}

/*
 * Constants useful for computation!
 */
var TOTAL_PEGS_TABLE [18]int = [18]int{0, 1, 3, 6, 10, 15, 21, 28, 36, 45, 55, 66, 78, 91, 105, 120, 136, 153}

/*
 * Definition of boardState. We pass this around everywhere!
 */
type boardState struct {
	board			[]bool
	initialPeg		int
	rows			int
	maxNumberOfPegs	int
	pegsLeft		int

	currentBest		int
	bestMoves		stack
	currMoves		stack

}

/*
 * Gets a peg number based on it's row and displacement within the row
 */
func GetPegNumber(row, disp int) int {
	if row < 0 || row >= len(TOTAL_PEGS_TABLE) || disp < 0 || disp > row {
		return -1
	}
	return TOTAL_PEGS_TABLE[row] + disp
}

/*
 * Gets the row of a peg based on the peg's number
 */
func GetRow(currentPeg int) int {
	var i int
	for i = 0; TOTAL_PEGS_TABLE[i] <= currentPeg; i++ {
	}
	return i - 1
}

/*
 * Get's the displacement within of a peg within a row based on the peg's number
 */
func GetDisplacement(currentPeg int) int {
	return currentPeg - TOTAL_PEGS_TABLE[GetRow(currentPeg)]
}

/*
 * Applies a move onto the board
 */
func ApplyMove(game *boardState, from, to, jump int) {
	game.board[from] = false
	game.board[to] = true
	game.board[jump] = false
	game.pegsLeft = game.pegsLeft - 1
	// Push new move to move stack
	move := make([]int, 2)
	move[0] = from + 1
	move[1] = to + 1
	game.currMoves.Push(move)
}

/*
 * Reverses a move on the board
 */
func ReverseMove(game *boardState, from, to, jump int) {
	game.board[from] = true
	game.board[to] = false
	game.board[jump] = true
	game.pegsLeft = game.pegsLeft + 1
	// Pop from move stack
	game.currMoves.Pop()
}

/*
 * Tests if a move is valid
 */
func TestMove(game *boardState, from, to, jump int) bool {
	lr := GetRow(to)
	if lr < 0 || lr >= game.rows {
		return false
	}

	ld := GetDisplacement(to)
	if ld < 0 || ld > lr {
		return false
	}


	return (game.board[from] && !game.board[to] && game.board[jump])

}

/*
 * Tests a move and applies it.
 */
func TestAndApply(game *boardState, from, to, jump int) bool {
	if TestMove(game, from, to, jump) {
		ApplyMove(game, from, to, jump)
		RecursiveSolve(game)
		ReverseMove(game, from, to, jump)
		return true
	}
	return false
}
/*
 * Tests and applies all possible moves givesn a peg.
 */
func TestNeighborMoves(game *boardState, currentPeg int) bool {
	validMove := false

	r := GetRow(currentPeg)
	d := GetDisplacement(currentPeg)
	var land, jump int

	land = GetPegNumber(r - 2, d)
	jump = GetPegNumber(r - 1, d)
	validMove = TestAndApply(game, currentPeg, land, jump) || validMove

	land = GetPegNumber(r, d + 2)
	jump = GetPegNumber(r, d + 1)
	validMove = TestAndApply(game, currentPeg, land, jump) || validMove

	land = GetPegNumber(r + 2, d + 2)
	jump = GetPegNumber(r + 1, d + 1)
	validMove = TestAndApply(game, currentPeg, land, jump) || validMove

	land = GetPegNumber(r + 2, d)
	jump = GetPegNumber(r + 1, d)
	validMove = TestAndApply(game, currentPeg, land, jump) || validMove

	land = GetPegNumber(r, d - 2)
	jump = GetPegNumber(r, d - 1)
	validMove = TestAndApply(game, currentPeg, land, jump) || validMove

	land = GetPegNumber(r - 2, d - 2)
	jump = GetPegNumber(r - 1, d - 1)
	validMove = TestAndApply(game, currentPeg, land, jump) || validMove

	return validMove
}

/*
 * Recursively solves a board
 */
func RecursiveSolve(game *boardState) {
	//fmt.Println("pegsLeft = ", game.pegsLeft)
	//fmt.Println("currentBest = ", game.currentBest)
	if game.pegsLeft > game.currentBest {
		validMove := false
		for i := 0; i < game.maxNumberOfPegs; i++ {
			if game.board[i] {
				validMove = TestNeighborMoves(game, i) || validMove
			}
		}
		if !validMove {
			game.currentBest = game.pegsLeft
			game.bestMoves = game.currMoves.Clone()
		}
	}
}

/*
 * Solves a board given the initial state of the board.
 */
func Solve(game *boardState, status chan int) {

	RecursiveSolve(game)
	status <- game.initialPeg
}

func usage() {
	fmt.Println("Usage: ./ConcurrentPegGame -s <rows>")
	fmt.Println("<rows> must be between 5 and 10, inclusive")
}

func main() {
	// Evaluate Command Line Arguments
	args := os.Args[1:]

	if len(args) != 2 {
		fmt.Println("Not enough arguments provided")
		usage()
		os.Exit(1)
	}

	if args[0] != "-s" {
		fmt.Println(args[0], " is not the correct flag.")
		usage()
		os.Exit(1)
	}

	rows, err := strconv.Atoi(args[1])

	if err != nil {
		fmt.Println(args[1], " is not a number")
		usage()
		os.Exit(1)
	} else if rows > 10 || rows < 5 {
		fmt.Println("The number of rows provided is not allowed")
		usage()
		os.Exit(1)
	}

	// Parallelize start states
	totalPegs := (rows * (rows + 1)) / 2
	rowsToCheck := (rows / 2) + 1
	pegsToCheck := (rowsToCheck * (rowsToCheck + 1)) / 2

	status := make(chan int, pegsToCheck)

	boards := make([]boardState, pegsToCheck)
	for i := 0; i < pegsToCheck; i++ {
		boards[i].initialPeg = i
		boards[i].rows = rows
		boards[i].maxNumberOfPegs = totalPegs
		boards[i].pegsLeft = totalPegs - 1
		boards[i].currentBest = 0
		boards[i].currMoves = NewStack()

		boards[i].board = make([]bool, totalPegs)
		for j := 0; j < totalPegs; j++ {
			if j == i {
				boards[i].board[j] = false
			} else {
				boards[i].board[j] = true
			}
		}

		go Solve(&boards[i], status)
	}

	// Wait for all thingies to finish
	for i := 0; i < pegsToCheck; i++ {
		<-status
	}

	// Find worst result
	var best int = 0
	for i := 0; i < pegsToCheck; i++ {
		if boards[i].currentBest > boards[best].currentBest {
			best = i
		}
	}

	var correctMoves stack = NewStack()
	for !boards[best].bestMoves.IsEmpty() {
		d, _ := boards[best].bestMoves.Pop()
		correctMoves.Push(d)
	}

	// Print out worst results ever
	fmt.Println((best + 1), correctMoves.Length())
	for !correctMoves.IsEmpty() {
		d, _ := correctMoves.Pop()
		fmt.Println(d)
	}
}
