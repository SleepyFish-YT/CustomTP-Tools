package me.sleepyfish.CTPT.ui;

import me.sleepyfish.CTPT.ui.impl.*;

import java.util.ArrayList;

public final class ToolManager {

    public ArrayList<Tool> tools = new ArrayList<>();

    public ToolManager() {
        this.tools.add(new NameEditTool());
        this.tools.add(new DupeFindTool());
        this.tools.add(new PositionSortTool());
        this.tools.add(new FileSplitterTool());
    }

}