package tf.tuff.netty;

import com.viaversion.viaversion.api.Via;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class BaseInjector {

	private final String handlerName;

	protected BaseInjector(String handlerName) {
		this.handlerName = handlerName;
	}

	protected abstract ChannelHandler createHandler(Player player);

	protected void onPostInject(Player player) {
	}

	public void inject(Player player) {
		UUID uuid = player.getUniqueId();
		var viaConnection = Via.getAPI().getConnection(uuid);
		if (viaConnection == null) return;

		Channel channel = viaConnection.getChannel();
		if (channel == null) return;

		channel.eventLoop().submit(() -> {
			try {
				if (channel.pipeline().get(handlerName) != null) {
					channel.pipeline().remove(handlerName);
				}

				String targetHandler = null;
				String[] anchors = {"packet_handler", "encoder", "via-encoder"};
				for (int i = 0; i < anchors.length; ++i) {
					if (channel.pipeline().get(anchors[i]) != null) {
						targetHandler = anchors[i];
						break;
					}
				}

				ChannelHandler handler = createHandler(player);
				if (targetHandler != null) {
					channel.pipeline().addBefore(targetHandler, handlerName, handler);
				} else {
					channel.pipeline().addFirst(handlerName, handler);
				}

				onPostInject(player);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public void eject(Player player) {
		UUID uuid = player.getUniqueId();
		var viaConnection = Via.getAPI().getConnection(uuid);
		if (viaConnection == null) return;

		Channel channel = viaConnection.getChannel();
		if (channel != null && channel.isOpen()) {
			channel.eventLoop().submit(() -> {
				try {
					if (channel.pipeline().get(handlerName) != null) {
						channel.pipeline().remove(handlerName);
					}
				} catch (Exception e) {
				}
			});
		}
	}
}
