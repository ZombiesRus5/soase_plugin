package sose.tools;

public interface SetupMonitor {

	void beginTask(String task, int amount);

	void subTask(String subTask);

	void finished(int amount);

}
