内存空间管理
---

*这部分代码位于 com.facebook.common.memory*

内存空间管理类似[硬盘空间管理](disk-space-management.md)，但是更**精确**。

同样是 `Trimmable`，但是回调方法 `trim` 带了类型 `MemoryTrimType`。

```java
public interface MemoryTrimmable {
  void trim(MemoryTrimType trimType);
}
```

```java
/**
 * Types of memory trim.
 *
 * <p>Each type of trim will provide a suggested trim ratio.
 *
 * <p>A {@link MemoryTrimmableRegistry} implementation sends out memory trim events with this type.
 */
public enum MemoryTrimType {

  /** The application is approaching the device-specific Java heap limit. */
  OnCloseToDalvikHeapLimit(0.5),

  /** The system as a whole is running out of memory, and this application is in the foreground. */
  OnSystemLowMemoryWhileAppInForeground(0.5),

  /** The system as a whole is running out of memory, and this application is in the background. */
  OnSystemLowMemoryWhileAppInBackground(1),

  /** This app is moving into the background, usually because the user navigated to another app. */
  OnAppBackgrounded(1);

  private double mSuggestedTrimRatio;

  private MemoryTrimType(double suggestedTrimRatio) {
    mSuggestedTrimRatio = suggestedTrimRatio;
  }

  /** Get the recommended percentage by which to trim the cache on receiving this event. */
  public double getSuggestedTrimRatio () {
    return mSuggestedTrimRatio;
  }
}
```

**Trim** 内存时会带上一个 `suggestedTrimRatio`，这样适用场景会更多，粒度也比 Disk 管理要细，因为 Disk 只有两个粒度而且不能改变（通过接口方法写死了）。

```java
public interface DiskTrimmable {
  void trimToMinimum();
  void trimToNothing();
}
```

`MemoryTrimmableRegistry` 的实现与 `SpaceTrimmableRegistry` 基本相同。

```java
public interface MemoryTrimmableRegistry {
  void registerMemoryTrimmable(MemoryTrimmable trimmable);
  void unregisterMemoryTrimmable(MemoryTrimmable trimmable);
}
```
