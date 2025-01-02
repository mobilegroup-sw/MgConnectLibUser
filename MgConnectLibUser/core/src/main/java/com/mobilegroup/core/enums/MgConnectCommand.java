package com.mobilegroup.core.enums;

public enum MgConnectCommand {
    Start("start"),
    DimaMode("DimaMode"),
    Pass("Pass"),
    BlockEngine("IgnDis"),
    UnlockEngine("IgnEn"),
    LockDoor("Lock"),
    UnlockDoor("Unlock"),
    VerifyKey("CheckKey"),
    ReplaceKey("ChangeKey"),
    GetCarData("GiveData"),
    Done("Done");

    private String command;

    MgConnectCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return this.command;
    }
}
