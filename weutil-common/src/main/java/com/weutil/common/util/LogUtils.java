package com.weutil.common.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;

/**
 * 日志格式化工具类
 *
 * <h2>说明
 * <p>提供格式化数据的工具方法，方便在日志信息中展示复杂的数据结构
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public final class LogUtils {

    /** 1KB 对应的字节数 */
    private static final int KB_SIZE = 1024;

    /** 10KB 对应的字节数 */
    private static final int TEN_KB_SIZE = 10 * 1024;

    /** 1MB 对应的字节数 */
    private static final int MB_SIZE = 1024 * 1024;

    /** 10MB 对应的字节数 */
    private static final int TEN_MB_SIZE = 10 * 1024 * 1024;

    /** 集合预览时的最大展示元素数 */
    private static final int DEFAULT_MAX_ELEMENTS = 8;

    // 私有构造函数，防止实例化
    private LogUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    /**
     * 预览任意类型数据为日志友好格式
     *
     * <h3>处理逻辑
     * <ul>
     *   <li>byte[]、BinaryMessage 或 String 类型：调用对应的 format 方法返回结果</li>
     *   <li>TextMessage 类型：调用对应的 format 方法返回结果</li>
     *   <li>PingMessage、PongMessage 类型：直接返回类名</li>
     *   <li>List、Set、Map、Object[] 类型：递归对元素调用 preview 方法，元素数量超过阈值时截断展示</li>
     *   <li>基本类型数组（int[]、long[] 等）：通过反射 API 遍历元素，递归调用 preview 方法</li>
     *   <li>Boolean、Character、Number 及其子类：格式化为 {@code Type(value)} 格式</li>
     *   <li>其他类型：调用 toString 方法并安全移除首尾双引号</li>
     * </ul>
     *
     * <h3>使用示例
     * <pre>{@code
     * preview(new byte[]{0x01, 0x02})
     * // 输出: byte[2](01 02)
     *
     * preview("hello")
     * // 输出: String[5](hello)
     *
     * preview(textMessage)
     * // 输出: TextMessage[5](hello)
     *
     * preview(binaryMessage)
     * // 输出: BinaryMessage[1KB]
     *
     * preview(pingMessage)
     * // 输出: PingMessage
     *
     * preview(pongMessage)
     * // 输出: PongMessage
     *
     * preview(messageList)
     * // 输出: List[size=2](TextMessage[5](hello), BinaryMessage[1KB])
     *
     * preview(new String[]{"a", "b"})
     * // 输出: Array[size=2](String[1](a), String[1](b))
     *
     * preview(new int[]{1, 2, 3})
     * // 输出: Array[size=3](Integer(1), Integer(2), Integer(3))
     *
     * preview(42)
     * // 输出: Integer(42)
     *
     * preview(3.14)
     * // 输出: Double(3.14)
     *
     * preview(customObject)
     * // 输出: CustomObject.toString() 的结果
     * }</pre>
     *
     * @param value 待预览的任意类型数据
     * @return 格式化后的字符串，若入参为 null 则返回 "null"
     */
    public static String preview(Object value) {
        return switch (value) {
            case null -> "null";
            case byte[] bytes -> format(bytes);
            case String str -> format(str);
            case TextMessage textMessage -> format(textMessage);
            case BinaryMessage binaryMessage -> format(binaryMessage);
            case PingMessage pingMessage -> format(pingMessage);
            case PongMessage pongMessage -> format(pongMessage);
            case List<?> list -> formatList(list);
            case Set<?> set -> format(set);
            case Map<?, ?> map -> format(map);
            case Object[] arr -> formatObjectArray(arr);
            case Boolean b -> "Boolean(" + b + ")";
            case Character c -> "Character(" + c + ")";
            case Number n -> n.getClass().getSimpleName() + "(" + n + ")";
            default -> {
                if (value.getClass().isArray()) {
                    yield formatPrimitiveArray(value);
                }
                yield value.toString().replace("\"", "");
            }
        };
    }

    /**
     * 格式化字节数组为日志友好格式
     *
     * <h3>格式说明
     * <p>输出格式为：{@code byte[n](xx xx xx xx ... xx xx xx xx)}
     * <ul>
     *   <li>n: 字节数量的友好展示，详见 {@code formatSize(int)}</li>
     *   <li>xx: 16进制字节内容预览
     *       <ul>
     *         <li>小于等于8字节：完整展示所有字节</li>
     *         <li>大于8字节：仅展示前4个和后4个字节，中间用 {@code ...} 分隔</li>
     *       </ul>
     *   </li>
     * </ul>
     *
     * <h3>使用示例
     * <pre>{@code
     * // 小于8字节
     * format(new byte[]{0x01, 0x02, 0x03})
     * // 输出: byte[3B](01 02 03)
     *
     * // 大于8字节
     * format(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A})
     * // 输出: byte[10B](01 02 03 04 ... 07 08 09 0A)
     *
     * // 大于1KB
     * format(largeByteArray)
     * // 输出: byte[9.5KB](...)
     * }</pre>
     *
     * @param data 待格式化的字节数组
     * @return 格式化后的字符串，若入参为 null 则返回 "byte[null]"
     */
    public static String format(byte[] data) {
        return format(data, 10);
    }

    /**
     * 格式化字节数组为日志友好格式（指定最大展示字节数）
     *
     * <h3>处理逻辑
     * <ul>
     *   <li>若字节数小于等于最大字节数，则完整展示所有字节</li>
     *   <li>若字节数大于最大字节数，则保留前后各一半，中间用 {@code ...} 分隔</li>
     * </ul>
     *
     * @param data 待格式化的字节数组
     * @param maxBytes 最大展示字节数
     * @return 格式化后的字符串
     */
    public static String format(byte[] data, int maxBytes) {
        if (data == null) {
            return "byte[null]";
        }

        int length = data.length;
        String sizeDisplay = formatSize(length);

        String content;
        if (length <= maxBytes) {
            content = formatBytes(data, 0, length);
        } else {
            int halfBytes = maxBytes / 2;
            String prefix = formatBytes(data, 0, halfBytes);
            String suffix = formatBytes(data, length - halfBytes, halfBytes);
            content = prefix + " ... " + suffix;
        }

        return "byte[" + sizeDisplay + "](" + content + ")";
    }

    /**
     * 格式化字符串为日志友好格式
     *
     * <h3>格式说明
     * <p>输出格式为：{@code String[n](content...content)}
     * <ul>
     *   <li>n: 字符串的字符数</li>
     *   <li>content: 字符串内容预览
     *       <ul>
     *         <li>少于60字符：完整展示所有内容</li>
     *         <li>大于等于60字符：仅展示前20个和后20个字符，中间用 {@code ...} 分隔</li>
     *       </ul>
     *   </li>
     * </ul>
     *
     * <h3>使用示例
     * <pre>{@code
     * // 少于60字符
     * format("hello")
     * // 输出: String[5](hello)
     *
     * // 大于等于60字符
     * format("hello world, this is a test string with more than sixty characters")
     * // 输出: String[70](hello world, this is a ... ixty characters)
     * }</pre>
     *
     * @param str 待格式化的字符串
     * @return 格式化后的字符串，若入参为 null 则返回 "String[null]"
     */
    public static String format(String str) {
        return format(str, 200);
    }

    /**
     * 格式化文本为单行缩略展示
     *
     * <h3>处理逻辑
     * <ul>
     *   <li>将所有换行符替换为 {@code ↩︎}</li>
     *   <li>若字符长度小于等于最大字符数，则完整展示</li>
     *   <li>若字符长度大于最大字符数，则保留前后各一半，中间用 {@code ......} 分隔，格式为 {@code String[n](xxxx......xxxx)}</li>
     * </ul>
     *
     * <h3>使用示例
     * <pre>{@code
     * // 不超过最大长度
     * format("hello\nworld", 20)
     * // 输出: hello↩︎world
     *
     * // 超过最大长度
     * format("hello world, this is a test", 10)
     * // 输出: String[27](hello......test)
     * }</pre>
     *
     * @param text 待格式化的文本
     * @param maxLength 最大字符数
     * @return 格式化后的字符串
     */
    public static String format(String text, int maxLength) {
        if (text == null) {
            return "String[null]";
        }

        String processed = text.replace("\n", "↩︎").replace("\r", "");
        int length = processed.length();

        String content;
        if (length <= maxLength) {
            content = processed;
        } else {
            int halfLength = maxLength / 2;
            String prefix = processed.substring(0, halfLength);
            String suffix = processed.substring(length - halfLength);
            content = prefix + "......" + suffix;
        }

        return "String[" + length + "](" + content + ")";
    }

    /**
     * 格式化 WebSocket 文本消息为日志友好格式
     *
     * <h3>格式说明
     * <p>输出格式为：{@code TextMessage[n](content......content)}
     * <ul>
     *   <li>n: 消息内容的字符数</li>
     *   <li>content: 消息内容预览
     *       <ul>
     *         <li>少于 200 字符：完整展示所有内容</li>
     *         <li>大于等于 200 字符：仅展示前 100 个和后 100 个字符，中间用 {@code ......} 分隔</li>
     *       </ul>
     *   </li>
     * </ul>
     *
     * <h3>使用示例
     * <pre>{@code
     * // 少于 200 字符
     * format(new TextMessage("hello"))
     * // 输出: TextMessage[5](hello)
     *
     * // 大于等于 200 字符
     * format(new TextMessage("很长的消息内容..."))
     * // 输出: TextMessage[300](前100字符......后100字符)
     * }</pre>
     *
     * @param message WebSocket 文本消息
     * @return 格式化后的字符串，若入参为 null 则返回 "TextMessage[null]"
     */
    public static String format(TextMessage message) {
        if (message == null) {
            return "TextMessage[null]";
        }

        String payload = message.getPayload();
        String processed = payload.replace("\n", "↩︎").replace("\r", "");
        int length = processed.length();

        String content;
        if (length <= 200) {
            content = processed;
        } else {
            String prefix = processed.substring(0, 100);
            String suffix = processed.substring(length - 100);
            content = prefix + "......" + suffix;
        }

        return "TextMessage[" + length + "](" + content + ")";
    }

    /**
     * 格式化 WebSocket 二进制消息为日志友好格式
     *
     * <h3>格式说明
     * <p>输出格式为：{@code BinaryMessage[n]} 或 {@code BinaryMessage[nKB]}
     * <ul>
     *   <li>n: 消息数据的字节数，详见 {@code formatSize(int)}</li>
     * </ul>
     *
     * <h3>使用示例
     * <pre>{@code
     * // 1KB 以下
     * format(binaryMessage)
     * // 输出: BinaryMessage[512B]
     *
     * // 1KB 及以上且 10KB 以下
     * format(binaryMessage)
     * // 输出: BinaryMessage[9.5KB]
     *
     * // 10KB 及以上
     * format(binaryMessage)
     * // 输出: BinaryMessage[12KB]
     *
     * // 1MB 及以上
     * format(binaryMessage)
     * // 输出: BinaryMessage[1.5MB]
     * }</pre>
     *
     * @param message WebSocket 二进制消息
     * @return 格式化后的字符串，若入参为 null 则返回 "BinaryMessage[null]"
     */
    public static String format(BinaryMessage message) {
        if (message == null) {
            return "BinaryMessage[null]";
        }

        int length = message.getPayload().remaining();
        return "BinaryMessage[" + formatSize(length) + "]";
    }

    /**
     * 格式化 WebSocket Ping 消息为日志友好格式
     *
     * <h3>格式说明
     * <p>直接返回类名 {@code PingMessage}，不包含变量部分
     *
     * @param message WebSocket Ping 消息
     * @return 格式化后的字符串，始终返回 "PingMessage"
     */
    public static String format(PingMessage message) {
        return "PingMessage";
    }

    /**
     * 格式化 WebSocket Pong 消息为日志友好格式
     *
     * <h3>格式说明
     * <p>直接返回类名 {@code PongMessage}，不包含变量部分
     *
     * @param message WebSocket Pong 消息
     * @return 格式化后的字符串，始终返回 "PongMessage"
     */
    public static String format(PongMessage message) {
        return "PongMessage";
    }

    /**
     * 格式化字节大小为友好展示
     *
     * <h3>处理逻辑
     * <ul>
     *   <li>1KB 以下：展示字节数并加 B 单位，如 {@code 512B}</li>
     *   <li>1KB 及以上且 10KB 以下：展示 KB 数并保留 1 位小数，如 {@code 9.5KB}</li>
     *   <li>10KB 及以上且 1MB 以下：展示 KB 数不保留小数，如 {@code 512KB}</li>
     *   <li>1MB 及以上且 10MB 以下：展示 MB 数并保留 1 位小数，如 {@code 9.5MB}</li>
     *   <li>10MB 及以上：展示 MB 数不保留小数，如 {@code 512MB}</li>
     * </ul>
     *
     * <h3>使用示例
     * <pre>{@code
     * formatSize(512)      // 输出: 512B
     * formatSize(1024)     // 输出: 1.0KB
     * formatSize(5120)     // 输出: 5.0KB
     * formatSize(10240)    // 输出: 10KB
     * formatSize(1048576)  // 输出: 1.0MB
     * formatSize(5242880)  // 输出: 5.0MB
     * formatSize(10485760) // 输出: 10MB
     * }</pre>
     *
     * @param length 字节数
     * @return 友好展示的大小字符串
     */
    public static String formatSize(int length) {
        // 1KB 以下：展示字节数并加 B 单位
        if (length < KB_SIZE) {
            return length + "B";
        }

        // 10MB 及以上：展示 MB 数不保留小数
        if (length >= TEN_MB_SIZE) {
            return (length / MB_SIZE) + "MB";
        }

        // 1MB 及以上且 10MB 以下：展示 MB 数并保留 1 位小数
        if (length >= MB_SIZE) {
            double mb = length / (double) MB_SIZE;
            return String.format("%.1fMB", mb);
        }

        // 10KB 及以上且 1MB 以下：展示 KB 数不保留小数
        if (length >= TEN_KB_SIZE) {
            return (length / KB_SIZE) + "KB";
        }

        // 1KB 及以上且 10KB 以下：展示 KB 数并保留 1 位小数
        double kb = length / (double) KB_SIZE;
        return String.format("%.1fKB", kb);
    }

    // ================================ private 方法 ================================

    /**
     * 格式化普通列表为日志友好格式
     *
     * <h3>处理逻辑
     * <p>对每个元素递归调用 {@code preview(Object)}，以逗号分隔展示。
     * 超过 {@code DEFAULT_MAX_ELEMENTS} 个元素时，仅展示首尾各一半，中间用 {@code ...} 分隔。
     *
     * @param list 待格式化的列表
     * @return 格式化后的字符串，格式为 {@code List[size=N](elem1, elem2, ...)}
     */
    private static String formatList(List<?> list) {
        int size = list.size();
        if (size == 0) {
            return "List[size=0]()";
        }
        if (size <= DEFAULT_MAX_ELEMENTS) {
            List<String> elements = list.stream().map(LogUtils::preview).toList();
            return "List[size=" + size + "](" + String.join(", ", elements) + ")";
        }
        int half = DEFAULT_MAX_ELEMENTS / 2;
        List<String> prefixElements = list.subList(0, half).stream().map(LogUtils::preview).toList();
        List<String> suffixElements = list.subList(size - half, size).stream().map(LogUtils::preview).toList();
        return "List[size=" + size + "]("
            + String.join(", ", prefixElements)
            + ", ..., "
            + String.join(", ", suffixElements)
            + ")";
    }

    /**
     * 格式化集合为日志友好格式
     *
     * <h3>处理逻辑
     * <p>对每个元素递归调用 {@code preview(Object)}，以逗号分隔展示。
     * 超过 {@code DEFAULT_MAX_ELEMENTS} 个元素时，仅展示前 N 个元素并追加 {@code ...}。
     *
     * @param set 待格式化的集合
     * @return 格式化后的字符串，格式为 {@code Set[size=N](elem1, elem2, ...)}
     */
    private static String format(Set<?> set) {
        int size = set.size();
        if (size == 0) {
            return "Set[size=0]()";
        }
        if (size <= DEFAULT_MAX_ELEMENTS) {
            List<String> elements = set.stream().map(LogUtils::preview).toList();
            return "Set[size=" + size + "](" + String.join(", ", elements) + ")";
        }
        List<String> elements = set.stream().limit(DEFAULT_MAX_ELEMENTS).map(LogUtils::preview).toList();
        return "Set[size=" + size + "](" + String.join(", ", elements) + ", ...)";
    }

    /**
     * 格式化映射为日志友好格式
     *
     * <h3>处理逻辑
     * <p>对每个 entry 递归调用 {@code preview(Object)}，格式为 {@code key=value}，以逗号分隔展示。
     * 超过 {@code DEFAULT_MAX_ELEMENTS} 个 entry 时，仅展示前 N 个并追加 {@code ...}。
     *
     * @param map 待格式化的映射
     * @return 格式化后的字符串，格式为 {@code Map[size=N](key1=val1, key2=val2, ...)}
     */
    private static String format(Map<?, ?> map) {
        int size = map.size();
        if (size == 0) {
            return "Map[size=0]()";
        }
        if (size <= DEFAULT_MAX_ELEMENTS) {
            List<String> entries = map.entrySet().stream()
                .map(e -> preview(e.getKey()) + "=" + preview(e.getValue()))
                .toList();
            return "Map[size=" + size + "](" + String.join(", ", entries) + ")";
        }
        List<String> entries = map.entrySet().stream()
            .limit(DEFAULT_MAX_ELEMENTS)
            .map(e -> preview(e.getKey()) + "=" + preview(e.getValue()))
            .toList();
        return "Map[size=" + size + "](" + String.join(", ", entries) + ", ...)";
    }

    /**
     * 格式化对象数组为日志友好格式
     *
     * <h3>处理逻辑
     * <p>对每个元素递归调用 {@code preview(Object)}，以逗号分隔展示。
     * 超过 {@code DEFAULT_MAX_ELEMENTS} 个元素时，仅展示首尾各一半，中间用 {@code ...} 分隔。
     *
     * @param array 待格式化的对象数组
     * @return 格式化后的字符串，格式为 {@code Array[size=N](elem1, elem2, ...)}
     */
    private static String formatObjectArray(Object[] array) {
        int length = array.length;
        if (length == 0) {
            return "Array[size=0]()";
        }
        if (length <= DEFAULT_MAX_ELEMENTS) {
            List<String> elements = Arrays.stream(array).map(LogUtils::preview).toList();
            return "Array[size=" + length + "](" + String.join(", ", elements) + ")";
        }
        int half = DEFAULT_MAX_ELEMENTS / 2;
        List<String> prefixElements = Arrays.stream(array).limit(half).map(LogUtils::preview).toList();
        List<String> suffixElements = Arrays.stream(array).skip(length - half).map(LogUtils::preview).toList();
        return "Array[size=" + length + "]("
            + String.join(", ", prefixElements)
            + ", ..., "
            + String.join(", ", suffixElements)
            + ")";
    }

    /**
     * 格式化基本类型数组为日志友好格式
     *
     * <h3>处理逻辑
     * <p>通过反射 API 遍历基本类型数组元素，递归调用 {@code preview(Object)}。
     * 超过 {@code DEFAULT_MAX_ELEMENTS} 个元素时，仅展示首尾各一半，中间用 {@code ...} 分隔。
     *
     * @param array 待格式化的基本类型数组
     * @return 格式化后的字符串，格式为 {@code Array[size=N](elem1, elem2, ...)}
     */
    private static String formatPrimitiveArray(Object array) {
        int length = Array.getLength(array);
        if (length == 0) {
            return "Array[size=0]()";
        }
        if (length <= DEFAULT_MAX_ELEMENTS) {
            List<String> elements = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                elements.add(preview(Array.get(array, i)));
            }
            return "Array[size=" + length + "](" + String.join(", ", elements) + ")";
        }
        int half = DEFAULT_MAX_ELEMENTS / 2;
        List<String> prefixElements = new ArrayList<>();
        for (int i = 0; i < half; i++) {
            prefixElements.add(preview(Array.get(array, i)));
        }
        List<String> suffixElements = new ArrayList<>();
        for (int i = length - half; i < length; i++) {
            suffixElements.add(preview(Array.get(array, i)));
        }
        return "Array[size=" + length + "]("
            + String.join(", ", prefixElements)
            + ", ..., "
            + String.join(", ", suffixElements)
            + ")";
    }

    /**
     * 将字节数组指定范围转换为16进制字符串
     *
     * @param data 字节数组
     * @param offset 起始偏移量
     * @param length 转换长度
     * @return 16进制字符串，字节间用空格分隔
     */
    private static String formatBytes(byte[] data, int offset, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sb.append(' ');
            }
            // 将字节转换为两位16进制字符串
            sb.append(String.format("%02X", data[offset + i] & 0xFF));
        }
        return sb.toString();
    }
}