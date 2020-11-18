package xyz.vopen.framework.registry.sync.nacos.event;

import lombok.*;
import xyz.vopen.framework.registry.sync.nacos.model.Service;
import xyz.vopen.mixmicro.kits.event.Event;

/**
 * {@link SyncedServiceRebuildEvent}
 *
 * <p>Class SyncedServiceRebuildEvent Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/18
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncedServiceRebuildEvent implements Event {

  @NonNull private Service service;

}
