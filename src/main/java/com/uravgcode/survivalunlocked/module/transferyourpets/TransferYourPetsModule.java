package com.uravgcode.survivalunlocked.module.transferyourpets;

import com.uravgcode.survivalunlocked.annotation.ModuleMeta;
import com.uravgcode.survivalunlocked.module.PluginModule;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.entity.Leashable;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@ModuleMeta(name = "transfer-your-pets")
public final class TransferYourPetsModule extends PluginModule {

    public TransferYourPetsModule(@NotNull JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPetTransfer(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player target)) return;

        var player = event.getPlayer();
        var playerId = player.getUniqueId();
        if (!player.isSneaking()) return;

        var leashedEntities = player.getNearbyEntities(16, 16, 16).stream()
            .filter(entity -> entity instanceof Leashable)
            .map(entity -> (Leashable) entity)
            .filter(Leashable::isLeashed)
            .filter(leashable -> playerId.equals(leashable.getLeashHolder().getUniqueId()))
            .toList();

        if (leashedEntities.isEmpty()) return;
        if (leashedEntities.stream()
            .filter(leashable -> leashable instanceof Tameable)
            .map(leashable -> (Tameable) leashable)
            .noneMatch(tameable -> playerId.equals(tameable.getOwnerUniqueId()))) {
            leashedEntities.forEach(leashable -> leashable.setLeashHolder(target));
            return;
        }

        showConfirmationDialog(player, target, (response, audience) -> {
            if (player.getLocation().distance(target.getLocation()) > 10) {
                player.sendActionBar(Component.text(target.getName() + " is too far away"));
                return;
            }

            for (var leashedEntity : leashedEntities) {
                if (!leashedEntity.isLeashed()) continue;
                leashedEntity.setLeashHolder(target);

                if (leashedEntity instanceof Tameable pet && playerId.equals(pet.getOwnerUniqueId())) {
                    pet.setOwner(target);
                }
            }
        });
    }

    @SuppressWarnings("UnstableApiUsage")
    private void showConfirmationDialog(Player player, Player target, DialogActionCallback callback) {
        var title = "Transfer Ownership";
        var body = """
            You are about to transfer ownership of your animal to %s.
            Do you want to continue?
            """.formatted(target.getName());

        var dialog = Dialog.create(builder -> builder
            .empty().base(DialogBase.builder(Component.text(title))
                .canCloseWithEscape(true)
                .body(List.of(DialogBody.plainMessage(Component.text(body))))
                .build()
            ).type(DialogType.confirmation(
                ActionButton.builder(Component.translatable("gui.yes"))
                    .action(DialogAction.customClick(callback, ClickCallback.Options.builder().build()))
                    .build(),
                ActionButton.builder(Component.translatable("gui.no"))
                    .build()
            ))
        );

        player.showDialog(dialog);
    }
}
