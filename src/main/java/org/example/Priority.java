package org.example;

public interface Priority {
    Thread getUpperThread(int target, int priority);
    Thread getLowerThread(int target, int priority);
}
