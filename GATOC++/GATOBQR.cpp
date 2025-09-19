
#include <iostream>
using namespace std;

char board[3][3];

void initBoard() {
    char c = '1';
    for (int i = 0; i < 3; ++i)
        for (int j = 0; j < 3; ++j)
            board[i][j] = c++;
}

void printBoard() {
    cout << "\n";
    for (int i = 0; i < 3; ++i) {
        cout << " ";
        for (int j = 0; j < 3; ++j) {
            cout << board[i][j];
            if (j < 2) cout << " | ";
        }
        cout << "\n";
        if (i < 2) cout << "-----------\n";
    }
    cout << "\n";
}

char checkWinner() {
    for (int i = 0; i < 3; ++i) {
        if (board[i][0] == board[i][1] && board[i][1] == board[i][2])
            return board[i][0];
        if (board[0][i] == board[1][i] && board[1][i] == board[2][i])
            return board[0][i];
    }
    if (board[0][0] == board[1][1] && board[1][1] == board[2][2])
        return board[0][0];
    if (board[0][2] == board[1][1] && board[1][1] == board[2][0])
        return board[0][2];
    return ' ';
}

bool isTie() {
    for (int i = 0; i < 3; ++i)
        for (int j = 0; j < 3; ++j)
            if (board[i][j] >= '1' && board[i][j] <= '9')
                return false;
    return true;
}

bool makeMove(int choice, char player) {
    if (choice < 1 || choice > 9) return false;
    int row = (choice - 1) / 3;
    int col = (choice - 1) % 3;
    if (board[row][col] == 'X' || board[row][col] == 'O') return false;
    board[row][col] = player;
    return true;
}

int main() {
    initBoard();
    char current = 'X';
    char winner = ' ';
    cout << "Juego del GATO - jugador X y jugador O\n";
    printBoard();

    while (true) {
        int move;
        cout << "Turno de " << current << ". Elige casilla (1-9): ";
        if (!(cin >> move)) {
            cin.clear();
            cin.ignore(10000, '\n');
            cout << "Entrada invalida.\n";
            continue;
        }
        if (!makeMove(move, current)) {
            cout << "Movimiento invalido.\n";
            continue;
        }
        printBoard();
        winner = checkWinner();
        if (winner == 'X') {
            cout << "Jugador X gana\n";
            cout << "Jugador O pierde\n";
            break;
        } else if (winner == 'O') {
            cout << "Jugador O gana\n";
            cout << "Jugador X pierde\n";
            break;
        } else if (isTie()) {
            cout << "Empate\n";
            break;
        }
        current = (current == 'X') ? 'O' : 'X';
    }
    return 0;
}
