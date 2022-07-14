package ru.winlocker.utils.commands.v2;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(staticName = "createEmpty")
@RequiredArgsConstructor(staticName = "create")
@Builder(buildMethodName = "create")
public class CommandDescription {

    private @NonNull String command;
    private String permission, description;

    private boolean onlyPlayers;

    public String getDescription() {
        return this.description != null ? this.description : this.command;
    }
}
