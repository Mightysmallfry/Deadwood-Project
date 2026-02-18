package mothman.viewports;

import mothman.sets.ActingRole;
import mothman.sets.GameSet;
import mothman.sets.SceneCard;
import mothman.utils.TurnDisplayInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ViewportText implements Viewport{

    private Scanner _sc = new Scanner(System.in);

    @Override
    public String GetName(){
        Scanner sc = new Scanner(System.in);
        System.out.print("Please input your name: ");
        String name = sc.nextLine();
        System.out.println("Thank You!");
        return name;
    }

    @Override
    public String GetMove(HashMap<String, GameSet> neighbors) {
        System.out.print("Available Locations: ");
        for (HashMap.Entry<String, GameSet> entry : neighbors.entrySet()) {
            System.out.print("[" + entry.getKey() + "] ");
        }
        System.out.println("or [cancel]");
        return _sc.nextLine().strip();
    }

    @Override
    public String GetRoleSelection(SceneCard sceneCard, ArrayList<ActingRole> localRoles) {
        System.out.println("Active Scene: " + sceneCard.GetName());

        System.out.print("Available Starring Roles: ");
        for (ActingRole role : sceneCard.GetAvailableRoles()) {
            System.out.print("[" + role.GetName() + ", rank: " + role.GetRank() + "] ");
        }
        System.out.println();

        System.out.print("Available Extra Roles: ");
        for (ActingRole role : localRoles) {
            System.out.print("[" + role.GetName() + ", rank: " + role.GetRank() + "] ");
        }
        System.out.println();
        System.out.println("[cancel] at anytime");

        Scanner sc = new Scanner(System.in);
        return sc.nextLine().strip();
    }

    @Override
    public String GetAction(ArrayList<String> possibleActions, TurnDisplayInfo info) {
        // Print whose turn it is
        System.out.println("||" + info.playerId + "'s Turn||");
        System.out.println("||Location: " + info.locationName + "||");

        if (info.isActingSet) {
            if (!info.sceneComplete) {
                if (info.roleLine != null) {
                    System.out.println(info.roleLine);
                }
                System.out.println("||Budget: " + info.budget + "||");
                System.out.println("||Total Shots: " + info.maxShots + "||");
                System.out.println("||Shot Count: " + info.currentShots + "||");
            } else {
                System.out.println("||Scene completed||");
            }
        }

        System.out.println("Action Points Available: " + info.actionTokens);

        DisplayActionList(possibleActions);

        System.out.print("Choice: ");
        return _sc.nextLine().toLowerCase().strip();
    }
    @Override
    public int GetUpgradeRank() {
        System.out.println("Enter the rank you want to purchase:");
        while (true) {
            try {
                return Integer.parseInt(_sc.nextLine().strip());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    @Override
    public String GetUpgradeCurrency() {
        System.out.println("Please enter payment type. [dollar] [credit]");
        return _sc.nextLine().strip();
    }

    @Override
    public void DisplayActionList(ArrayList<String> actionList) {
        System.out.print("Available actions : ");
        for (int i = 0; i < actionList.size(); i++) {
            System.out.print("[" + actionList.get(i) + "]");

            if (i != actionList.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println();
    }

    @Override
    public void DisplayMessage(String message) {
        System.out.println(message);
    }

}
