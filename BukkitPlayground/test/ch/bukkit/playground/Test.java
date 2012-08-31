package ch.bukkit.playground;

import ch.bukkit.playground.instant.InstantHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {

    public static void main(String[] args) throws IOException {
        InstantHandler instantHandler = new InstantHandler();
        instantHandler.start();

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String s;
        while ((s = in.readLine()) != null) {
            if ("exit".equals(s)) System.exit(0);

            if (s.startsWith("create")) {
                s = s.replace("create ", "");
                instantHandler.handleOpCommands(s, "stat", null, null);
            }
        }

    }
}

