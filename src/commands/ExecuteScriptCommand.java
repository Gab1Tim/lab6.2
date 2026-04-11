package commands;

import managers.CommandManager;
import managers.InputManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Команда {@code execute_script}: выполняет команды из файла.
 */
public class ExecuteScriptCommand implements Command {

    private final CommandManager commandManager;
    /** Множество сейчас выполняемых скриптов (по каноническому пути). */
    private static final Set<String> executingScripts = new HashSet<>();

    /** Создаёт команду. */
    public ExecuteScriptCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public String getName() {
        return "execute_script";
    }

    @Override
    public String getDescription() {
        return "execute_script file_name : execute commands from the specified file";
    }

    @Override
    /** Запускает выполнение скрипта. */
    public void execute(String[] args) {

        if (args.length < 1) {
            System.out.println("Usage: execute_script <file_name>");
            return;
        }

        String fileName = args[0];
        File file = new File(fileName);

        String canonicalPath;
        try {
            canonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            System.out.println("Cannot resolve script path: " + e.getMessage());
            return;
        }

        if (executingScripts.contains(canonicalPath)) {
            System.out.println("Recursive script call is not allowed: " + fileName);
            return;
        }

        if (!file.exists()) {
            System.out.println("File not found: " + fileName);
            return;
        }

        if (!file.canRead()) {
            System.out.println("Cannot read file (permission denied): " + fileName);
            return;
        }

        executingScripts.add(canonicalPath);

        try {
            Scanner fileScanner = new Scanner(file);
            InputManager.setFileInput(fileScanner);

            System.out.println("Running script: " + fileName);

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty()) continue;

                System.out.println("> " + line);

                try {
                    commandManager.executeCommand(line);
                } catch (InputManager.ScriptInputException e) {
                    System.out.println("Script error: " + e.getMessage());
                    System.out.println("Script stopped. Please fix your script and try again.");
                    break;
                } catch (Exception e) {
                    System.out.println("Script error: " + e.getMessage());
                    System.out.println("Script stopped. Please fix your script and try again.");
                    break;
                }
            }

            fileScanner.close();
            System.out.println("Script finished: " + fileName);

        } catch (FileNotFoundException e) {
            System.out.println("Error reading file: " + e.getMessage());
        } finally {
            executingScripts.remove(canonicalPath);
            InputManager.restoreConsoleInput();
        }
    }
}