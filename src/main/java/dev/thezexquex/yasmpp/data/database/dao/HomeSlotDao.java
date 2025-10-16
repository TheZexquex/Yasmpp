package dev.thezexquex.yasmpp.data.database.dao;

import de.chojo.sadu.queries.api.call.Call;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.chojo.sadu.queries.call.adapter.UUIDAdapter;
import de.unknowncity.astralib.common.database.QueryConfigHolder;
import org.intellij.lang.annotations.Language;

import java.util.Optional;
import java.util.UUID;

public class HomeSlotDao extends QueryConfigHolder {

    public HomeSlotDao(QueryConfiguration config) {
        super(config);
    }

    public boolean incrementHomeSlot(UUID playerId, int amount) {
        @Language("sqlite")
        var query = """
                INSERT INTO home_slots (player_uuid, slot_id) VALUES (:player_id, :amount) ON CONFLICT DO UPDATE SET slot_id = slot_id + :amount WHERE player_uuid = :player_id;
                """;
        return config.query(query)
                .single(Call.call()
                        .bind("player_id", playerId, UUIDAdapter.AS_STRING)
                        .bind("amount", amount)
                )
                .update()
                .changed();
    }

    public Optional<Integer> getMaxHomeSlot(UUID playerId) {
        @Language("sqlite")
        var query = """
                SELECT slot_id FROM home_slots WHERE player_uuid = :player_id
                """;
        return config.query(query)
                .single(Call.call().bind("player_id", playerId, UUIDAdapter.AS_STRING))
                .map(row -> row.getInt("slot_id"))
                .first();
    }
}
