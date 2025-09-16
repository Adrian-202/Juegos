

FILAS = 6
COLUMNAS = 7
tablero = [[" " for _ in range(COLUMNAS)] for _ in range(FILAS)]
jugador = "X"


def imprimir_tablero():
    for fila in tablero:
        print(" ".join(fila))
    print("1 2 3 4 5 6 7")


def soltar_ficha(col):
    for f in range(FILAS - 1, -1, -1):
        if tablero[f][col] == " ":
            tablero[f][col] = jugador
            return True
    return False


def ganador():
    # horizontal
    for f in range(FILAS):
        for c in range(COLUMNAS - 3):
            if tablero[f][c] == tablero[f][c+1] == tablero[f][c+2] == tablero[f][c+3] != " ":
                return True
    # vertical
    for f in range(FILAS - 3):
        for c in range(COLUMNAS):
            if tablero[f][c] == tablero[f+1][c] == tablero[f+2][c] == tablero[f+3][c] != " ":
                return True
    # diagonal \
    for f in range(FILAS - 3):
        for c in range(COLUMNAS - 3):
            if tablero[f][c] == tablero[f+1][c+1] == tablero[f+2][c+2] == tablero[f+3][c+3] != " ":
                return True
    # diagonal /
    for f in range(3, FILAS):
        for c in range(COLUMNAS - 3):
            if tablero[f][c] == tablero[f-1][c+1] == tablero[f-2][c+2] == tablero[f-3][c+3] != " ":
                return True
    return False


def lleno():
    return all(tablero[0][c] != " " for c in range(COLUMNAS))


def cambiar():
    global jugador
    jugador = "O" if jugador == "X" else "X"


def jugar():
    global jugador
    while True:
        imprimir_tablero()
        col = input(f"Turno {jugador}, elige columna (1-7): ")

        if not col.isdigit():
            print("Número inválido.")
            continue

        col = int(col) - 1
        if col < 0 or col >= COLUMNAS or not soltar_ficha(col):
            print("Movimiento inválido.")
            continue

        if ganador():
            imprimir_tablero()
            print(f"Gana {jugador}")
            break
        if lleno():
            imprimir_tablero()
            print("Empate")
            break

        cambiar()


if __name__ == "__main__":
    jugar()
