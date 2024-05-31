package com.example.app.Utils;

import android.os.Bundle;

import androidx.navigation.NavController;

import java.util.Stack;

public class Navigator {

    public static final String TYPE_FRAG = "fragment";
    public enum TypeAct {MAIN, ADD}

    public enum CURR_TYPE {START, INFO, MY_PROD, PROD, FEEDBACK, ABOUT}

    public static final String ID_PROD = "ID_PROD";


    private static final Navigator INSTANCE = new Navigator();
    private volatile NavController navController;
    private final Stack<Integer> stack;

    private TypeAct type;


    private Navigator() {
        navController = null;
        stack = new Stack<>();
    }

    public static Navigator getInstance() {
        return INSTANCE;
    }

    public void setNavController(NavController Controller, TypeAct act) {
        navController = Controller;
        if (type != act) {
            type = act;
            stack.clear();
        }
    }

    public NavController getNavController() {
        return navController;
    }

    public void navigateTo(int id) {
        if (inStack(id)) return;
        navController.navigate(id);
        stack.push(id);
    }

    public void navigateTo(int id, Bundle arg) {
        if (inStack(id)) return;
        navController.navigate(id, arg);
        stack.push(id);
    }

    public void popBackStack() {
        navController.popBackStack();
        if (!stack.isEmpty()) stack.pop();
    }

    public void popBackStack(int id, boolean inclusive) {
        navController.popBackStack(id, inclusive);
        while (!stack.isEmpty() && stack.peek() != id) stack.pop();
        if (inclusive && stack.peek() == id) stack.pop();
    }

    public static boolean wasMain = false;

    public static boolean toBucket = false;

    public int getStackSize() { return stack.size(); }

    public boolean inStack(int id) { return stack.search(id) != -1; }

    public void pushStack(int id) { stack.push(id); }

    public void clearStack() { stack.clear(); }

}
