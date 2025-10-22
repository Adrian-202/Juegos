using System;

class Program
{
    static Random rnd = new Random();

    static void Main()
    {
        const int N = 10;
        int[] barcos = { 5, 4, 3, 3, 2 };
        char[,] tablero = new char[N, N];
        char[,] visible = new char[N, N];

        Inicializar(tablero);
        Inicializar(visible);
        ColocarBarcos(tablero, barcos);

        int restantes = 0;
        foreach (int t in barcos) restantes += t;

        Console.WriteLine("=== BATTLESHIP  ===");
        Console.WriteLine("Dispara con: fila columna (ej. 4 7)\n");

        while (restantes > 0)
        {
            Mostrar(visible);
            Console.Write("Disparo: ");
            string[] p = Console.ReadLine().Split(' ', StringSplitOptions.RemoveEmptyEntries);

            if (p.Length != 2 || !int.TryParse(p[0], out int f) || !int.TryParse(p[1], out int c)
                || f < 0 || f >= N || c < 0 || c >= N)
            {
                Console.WriteLine("Coordenada invalida. ");
                continue;
            }

            if (visible[f, c] != '~')
            {
                Console.WriteLine("Ya disparaste ahi. ");
                continue;
            }

            if (tablero[f, c] == 'B')
            {
                Console.WriteLine(" ¡Impacto! ");
                visible[f, c] = 'X';
                tablero[f, c] = 'X';
                restantes--;
            }
            else
            {
                Console.WriteLine("Agua...");
                visible[f, c] = 'O';
            }
        }

        Console.Clear();
        Console.WriteLine("Ganaste Hundiste todos los barcos.\nTablero final:");
        Mostrar(visible);
    }

    static void Inicializar(char[,] t)
    {
        for (int i = 0; i < t.GetLength(0); i++)
            for (int j = 0; j < t.GetLength(1); j++)
                t[i, j] = '~';
    }

    static void Mostrar(char[,] t)
    {
        int n = t.GetLength(0);
        Console.Write("   ");
        for (int j = 0; j < n; j++) Console.Write(j + " ");
        Console.WriteLine();
        for (int i = 0; i < n; i++)
        {
            Console.Write(i.ToString().PadLeft(2) + " ");
            for (int j = 0; j < n; j++)
                Console.Write(t[i, j] + " ");
            Console.WriteLine();
        }
    }

    static void ColocarBarcos(char[,] tablero, int[] barcos)
    {
        int n = tablero.GetLength(0);
        foreach (int tam in barcos)
        {
            bool colocado = false;
            while (!colocado)
            {
                int f = rnd.Next(n), c = rnd.Next(n);
                bool horiz = rnd.Next(2) == 0;

                if (horiz)
                {
                    if (c + tam > n) continue;
                    bool libre = true;
                    for (int k = 0; k < tam; k++)
                        if (tablero[f, c + k] == 'B') { libre = false; break; }
                    if (!libre) continue;
                    for (int k = 0; k < tam; k++) tablero[f, c + k] = 'B';
                    colocado = true;
                }
                else
                {
                    if (f + tam > n) continue;
                    bool libre = true;
                    for (int k = 0; k < tam; k++)
                        if (tablero[f + k, c] == 'B') { libre = false; break; }
                    if (!libre) continue;
                    for (int k = 0; k < tam; k++) tablero[f + k, c] = 'B';
                    colocado = true;
                }
            }
        }
    }
}
