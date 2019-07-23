/*
 * This software Copyright by the RPTools.net development team, and
 * licensed under the Affero GPL Version 3 or, at your option, any later
 * version.
 *
 * MapTool Source Code is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU Affero General Public
 * License * along with this source Code.  If not, please visit
 * <http://www.gnu.org/licenses/> and specifically the Affero license
 * text at <http://www.gnu.org/licenses/agpl.html>.
 */
package net.rptools.maptool.client;

import java.io.IOException;
import java.net.Socket;
import net.rptools.clientserver.hessian.client.ClientConnection;
import net.rptools.maptool.model.Player;
import net.rptools.maptool.server.Handshake;
import net.rptools.maptool.server.ServerPolicy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** @author trevor */
public class MapToolConnection extends ClientConnection {
  private static final Logger log = LogManager.getLogger(MapToolConnection.class);
  private final Player player;

  public MapToolConnection(String host, int port, Player player) throws IOException {
    super(host, port, null);
    this.player = player;
  }

  public MapToolConnection(Socket socket, Player player) throws IOException {
    super(socket, null);
    this.player = player;
  }

  private String version;
  /** Overrides the version in MapTool.getVerion() to be a specific version */
  public void SetMapToolVersion(String version) {
    this.version = version;
  }

  private ServerPolicy serverPolicy;
  /**
   * Gets the policy of the remote server
   *
   * @return
   */
  public ServerPolicy getServerPolicy() {
    return serverPolicy;
  }

  /*
   * (non-Javadoc)
   *
   * @see net.rptools.clientserver.simple.client.ClientConnection#sendHandshake( java.net.Socket)
   */
  @Override
  public boolean sendHandshake(Socket s) throws IOException {
    log.info("Shaking hands");
    if (version == null) {
      version = MapTool.getVersion();
    }
    log.info("MapTool version is " + version);

    Handshake.Response response =
        Handshake.sendHandshake(
            new Handshake.Request(
                player.getName(), player.getPassword(), player.getRole(), version),
            s);

    if (response.code != Handshake.Code.OK) {
      log.error("Handshake error: " + response.message);
      // MapTool.showError("ERROR: " + response.message);
      return false;
    }
    log.info("Handshake ok");
    boolean result = response.code == Handshake.Code.OK;
    if (result) {
      // MapTool.setServerPolicy(response.policy);
      serverPolicy = response.policy;
    }
    return result;
  }
}
