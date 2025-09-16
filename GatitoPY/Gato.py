
tablero = [str(i) for i in range(1, 10)]
jugador_actual = "X"


def imprimir_tablero():
    print("-------------")
    for i in range(0, 9, 3):
        print(f"| {tablero[i]} | {tablero[i+1]} | {tablero[i+2]} |")
        print("-------------")


def hay_ganador():
  
    combinaciones = [
        [0, 1, 2], [3, 4, 5], [6, 7, 8],  
        [0, 3, 6], [1, 4, 7], [2, 5, 8],  
        [0, 4, 8], [2, 4, 6]              
    ]
    for combo in combinaciones:
        if tablero[combo[0]] == tablero[combo[1]] == tablero[combo[2]]:
            return True
    return False


def tablero_lleno():
    return all(c in ["X", "O"] for c in tablero)


def cambiar_jugador():
    global jugador_actual
    jugador_actual = "O" if jugador_actual == "X" else "X"


def jugar():
    global jugador_actual
    while True:
        imprimir_tablero()
        print(f"Turno del jugador {jugador_actual}")

        try:
            pos = int(input("ELIGE UN NUMERO DISPONIBLE: ")) - 1
        except ValueError:
            print("❌ Entrada inválida, usa solo números Disponibles.")
            continue

        if 0 <= pos < 9 and tablero[pos] not in ["X", "O"]:
            tablero[pos] = jugador_actual
        else:
            print("❌ Movimiento inválido, intenta de nuevo.")
            continue

        if hay_ganador():
            imprimir_tablero()
            print(f"🎉 ¡Jugador {jugador_actual} ganó!")
            break
        elif tablero_lleno():
            imprimir_tablero()
            print("🤝 ¡Empate!")
            break
        else:
            cambiar_jugador()


if __name__ == "__main__":
    jugar()
