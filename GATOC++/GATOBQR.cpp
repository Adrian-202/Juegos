
#include <iostream>
using namespace std;

char tablero[3][3] = {
    {'1', '2', '3'},
    {'4', '5', '6'},
    {'7', '8', '9'}
};

char jugador = 'X';
int movimiento;
bool ganador = false;

void mostrarTablero() {
    cout << "\n";
    cout << " " << tablero[0][0] << " | " << tablero[0][1] << " | " << tablero[0][2] << "\n";
    cout << "---|---|---\n";
    cout << " " << tablero[1][0] << " | " << tablero[1][1] << " | " << tablero[1][2] << "\n";
    cout << "---|---|---\n";
    cout << " " << tablero[2][0] << " | " << tablero[2][1] << " | " << tablero[2][2] << "\n";
}

bool verificarGanador() {

    for (int i = 0; i < 3; i++) {
        if (tablero[i][0] == tablero[i][1] && tablero[i][1] == tablero[i][2]) {
            return true;
        }
    }

    for (int i = 0; i < 3; i++) {
        if (tablero[0][i] == tablero[1][i] && tablero[1][i] == tablero[2][i]) {
            return true;
        }
    }

    if (tablero[0][0] == tablero[1][1] && tablero[1][1] == tablero[2][2]) {
        return true;
    }
    if (tablero[0][2] == tablero[1][1] && tablero[1][1] == tablero[2][0]) {
        return true;
    }
    return false;
}

void jugar() {
    while (!ganador) {
        mostrarTablero();
        cout << "Jugador " << jugador << ", ingrese el número de la casilla (1-9): ";
        cin >> movimiento;

        if (movimiento < 1 || movimiento > 9) {
            cout << "Número inválido. Intenta nuevamente.\n";
            continue;
        }

        int fila = (movimiento - 1) / 3;
        int columna = (movimiento - 1) % 3;

        if (tablero[fila][columna] == 'X' || tablero[fila][columna] == 'O') {
            cout << "Casilla ya ocupada. Intenta nuevamente.\n";
            continue;
        }

        tablero[fila][columna] = jugador;

        if (verificarGanador()) {
            mostrarTablero();
            cout << "¡El jugador " << jugador << " ha ganado!\n";
            ganador = true;
        } else {
            jugador = (jugador == 'X') ? 'O' : 'X';
        }
    }
}

int main() {
    cout << "Bienvenido al Juego del Gato \n";
    jugar();
    return 0;
}
