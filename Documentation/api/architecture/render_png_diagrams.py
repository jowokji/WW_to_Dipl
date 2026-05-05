from __future__ import annotations

import math
from pathlib import Path

from PIL import Image, ImageDraw, ImageFont


ROOT = Path(__file__).resolve().parent
OUT = ROOT / "png"

NAVY = "#1f2937"
BLUE = "#2563eb"
LIGHT_BLUE = "#dbeafe"
GREEN = "#059669"
LIGHT_GREEN = "#d1fae5"
AMBER = "#d97706"
LIGHT_AMBER = "#fef3c7"
RED = "#dc2626"
LIGHT_RED = "#fee2e2"
PURPLE = "#7c3aed"
LIGHT_PURPLE = "#ede9fe"
GREY = "#6b7280"
LIGHT_GREY = "#f3f4f6"
WHITE = "#ffffff"
BLACK = "#111827"


def font(size: int, bold: bool = False) -> ImageFont.FreeTypeFont | ImageFont.ImageFont:
    candidates = [
        Path("C:/Windows/Fonts/seguisb.ttf" if bold else "C:/Windows/Fonts/segoeui.ttf"),
        Path("C:/Windows/Fonts/arialbd.ttf" if bold else "C:/Windows/Fonts/arial.ttf"),
    ]
    for candidate in candidates:
        if candidate.exists():
            return ImageFont.truetype(str(candidate), size)
    return ImageFont.load_default()


TITLE = font(34, True)
SUBTITLE = font(20)
LABEL = font(20, True)
TEXT = font(18)
SMALL = font(15)


def text_size(draw: ImageDraw.ImageDraw, text: str, fnt: ImageFont.ImageFont) -> tuple[int, int]:
    box = draw.textbbox((0, 0), text, font=fnt)
    return box[2] - box[0], box[3] - box[1]


def wrap_text(
    draw: ImageDraw.ImageDraw,
    text: str,
    fnt: ImageFont.ImageFont,
    max_width: int,
) -> list[str]:
    words = text.split()
    lines: list[str] = []
    current = ""
    for word in words:
        trial = word if not current else f"{current} {word}"
        if text_size(draw, trial, fnt)[0] <= max_width:
            current = trial
        else:
            if current:
                lines.append(current)
            current = word
    if current:
        lines.append(current)
    return lines or [text]


def draw_centered_text(
    draw: ImageDraw.ImageDraw,
    box: tuple[int, int, int, int],
    text: str,
    fnt: ImageFont.ImageFont,
    fill: str = BLACK,
    max_width: int | None = None,
    line_gap: int = 6,
) -> None:
    max_width = max_width or (box[2] - box[0] - 24)
    lines = wrap_text(draw, text, fnt, max_width)
    heights = [text_size(draw, line, fnt)[1] for line in lines]
    total_height = sum(heights) + line_gap * (len(lines) - 1)
    y = box[1] + ((box[3] - box[1]) - total_height) / 2
    for line, height in zip(lines, heights):
        width, _ = text_size(draw, line, fnt)
        x = box[0] + ((box[2] - box[0]) - width) / 2
        draw.text((x, y), line, font=fnt, fill=fill)
        y += height + line_gap


def rounded_box(
    draw: ImageDraw.ImageDraw,
    box: tuple[int, int, int, int],
    text: str,
    fill: str,
    outline: str = NAVY,
    text_fill: str = BLACK,
    fnt: ImageFont.ImageFont = LABEL,
    radius: int = 18,
) -> None:
    draw.rounded_rectangle(box, radius=radius, fill=fill, outline=outline, width=3)
    draw_centered_text(draw, box, text, fnt, text_fill)


def diamond(
    draw: ImageDraw.ImageDraw,
    center: tuple[int, int],
    size: tuple[int, int],
    text: str,
    fill: str,
    outline: str = NAVY,
) -> tuple[int, int, int, int]:
    cx, cy = center
    w, h = size
    points = [(cx, cy - h // 2), (cx + w // 2, cy), (cx, cy + h // 2), (cx - w // 2, cy)]
    draw.polygon(points, fill=fill, outline=outline)
    draw.line(points + [points[0]], fill=outline, width=3)
    box = (cx - w // 2 + 22, cy - h // 2 + 22, cx + w // 2 - 22, cy + h // 2 - 22)
    draw_centered_text(draw, box, text, TEXT)
    return (cx - w // 2, cy - h // 2, cx + w // 2, cy + h // 2)


def arrow(
    draw: ImageDraw.ImageDraw,
    start: tuple[int, int],
    end: tuple[int, int],
    fill: str = NAVY,
    width: int = 4,
    head: int = 14,
) -> None:
    draw.line([start, end], fill=fill, width=width)
    angle = math.atan2(end[1] - start[1], end[0] - start[0])
    left = (
        end[0] - head * math.cos(angle - math.pi / 6),
        end[1] - head * math.sin(angle - math.pi / 6),
    )
    right = (
        end[0] - head * math.cos(angle + math.pi / 6),
        end[1] - head * math.sin(angle + math.pi / 6),
    )
    draw.polygon([end, left, right], fill=fill)


def labeled_arrow(
    draw: ImageDraw.ImageDraw,
    start: tuple[int, int],
    end: tuple[int, int],
    text: str | None = None,
    fill: str = NAVY,
) -> None:
    arrow(draw, start, end, fill=fill)
    if text:
        x = (start[0] + end[0]) // 2
        y = (start[1] + end[1]) // 2 - 24
        w, h = text_size(draw, text, SMALL)
        draw.rounded_rectangle((x - w // 2 - 8, y - 4, x + w // 2 + 8, y + h + 4), 8, fill=WHITE)
        draw.text((x - w // 2, y), text, font=SMALL, fill=fill)


def base_canvas(width: int, height: int, title: str, subtitle: str) -> tuple[Image.Image, ImageDraw.ImageDraw]:
    image = Image.new("RGB", (width, height), WHITE)
    draw = ImageDraw.Draw(image)
    draw.rectangle((0, 0, width, 94), fill=LIGHT_GREY)
    draw.text((48, 24), title, font=TITLE, fill=NAVY)
    draw.text((50, 62), subtitle, font=SUBTITLE, fill=GREY)
    return image, draw


def save(image: Image.Image, name: str) -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    image.save(OUT / name, "PNG", optimize=True)


def render_system_context() -> None:
    image, draw = base_canvas(
        1500,
        760,
        "WeatherWear API - System Context",
        "External actors, runtime API, persistence, and third-party dependencies.",
    )
    boxes = {
        "client": (80, 270, 330, 390),
        "api": (590, 255, 910, 410),
        "db": (1110, 150, 1380, 270),
        "weather": (1110, 330, 1380, 450),
        "llm": (1110, 510, 1380, 630),
        "dev": (80, 520, 330, 640),
        "swagger": (590, 520, 910, 640),
    }
    rounded_box(draw, boxes["client"], "Mobile or web client", LIGHT_BLUE, BLUE)
    rounded_box(draw, boxes["api"], "WeatherWear REST API", LIGHT_GREEN, GREEN)
    rounded_box(draw, boxes["db"], "PostgreSQL", LIGHT_AMBER, AMBER)
    rounded_box(draw, boxes["weather"], "OpenWeather API", LIGHT_AMBER, AMBER)
    rounded_box(draw, boxes["llm"], "LLM API", LIGHT_AMBER, AMBER)
    rounded_box(draw, boxes["dev"], "Developer", LIGHT_PURPLE, PURPLE)
    rounded_box(draw, boxes["swagger"], "Swagger UI and OpenAPI docs", LIGHT_PURPLE, PURPLE)

    labeled_arrow(draw, (330, 330), (590, 330), "HTTPS JSON")
    labeled_arrow(draw, (910, 320), (1110, 210), "JPA")
    labeled_arrow(draw, (910, 335), (1110, 390), "weather lookup")
    labeled_arrow(draw, (910, 365), (1110, 570), "AI generation")
    labeled_arrow(draw, (330, 580), (590, 580), "reads contract")
    labeled_arrow(draw, (750, 520), (750, 410), "try it out", PURPLE)
    save(image, "system-context.png")


def render_component_diagram() -> None:
    image, draw = base_canvas(
        1800,
        1280,
        "WeatherWear API - Component Diagram",
        "Backend modules, repositories, DTO contracts, security, and external services.",
    )
    client = (70, 150, 330, 245)
    security = (450, 150, 760, 245)
    controllers = (880, 150, 1190, 245)
    dtos = (1360, 150, 1700, 245)
    errors = (1360, 310, 1700, 405)

    service_group = (70, 350, 1190, 640)
    integration_group = (70, 760, 1700, 910)
    external_group = (70, 1050, 1700, 1190)

    rounded_box(draw, client, "Mobile or web client", LIGHT_BLUE, BLUE)
    rounded_box(draw, security, "Spring Security + JwtAuthFilter", LIGHT_BLUE, BLUE)
    rounded_box(draw, controllers, "REST Controllers", LIGHT_BLUE, BLUE)
    rounded_box(draw, dtos, "Request and response DTOs", LIGHT_PURPLE, PURPLE)
    rounded_box(draw, errors, "GlobalExceptionHandler + ErrorResponse", LIGHT_RED, RED)

    draw.rounded_rectangle(service_group, 18, fill="#ecfdf5", outline=GREEN, width=3)
    draw.text((95, 370), "Service layer", font=LABEL, fill=GREEN)
    service_boxes = [
        ("AuthService", (110, 430, 330, 510)),
        ("UserService", (370, 430, 590, 510)),
        ("WeatherService", (630, 430, 850, 510)),
        ("RecommendationService", (890, 430, 1150, 510)),
        ("PreferenceService", (110, 540, 330, 620)),
        ("HistoryService", (370, 540, 590, 620)),
        ("FeedbackService", (630, 540, 850, 620)),
        ("ChatService", (890, 540, 1150, 620)),
    ]
    for label, box in service_boxes:
        rounded_box(draw, box, label, LIGHT_GREEN, GREEN, fnt=SMALL)

    draw.rounded_rectangle(integration_group, 18, fill=LIGHT_GREY, outline=NAVY, width=3)
    draw.text((95, 780), "Internal dependencies", font=LABEL, fill=NAVY)
    dependency_boxes = {
        "jwt": (110, 830, 370, 890),
        "weather_client": (430, 830, 720, 890),
        "llm": (780, 830, 1040, 890),
        "repos": (1100, 830, 1390, 890),
        "weather_cache": (1450, 830, 1660, 890),
    }
    rounded_box(draw, dependency_boxes["jwt"], "JwtService", WHITE, NAVY, fnt=SMALL)
    rounded_box(draw, dependency_boxes["weather_client"], "WeatherApiClient", WHITE, NAVY, fnt=SMALL)
    rounded_box(draw, dependency_boxes["llm"], "LlmClient", WHITE, NAVY, fnt=SMALL)
    rounded_box(draw, dependency_boxes["repos"], "Spring Data JPA repositories", WHITE, NAVY, fnt=SMALL)
    rounded_box(draw, dependency_boxes["weather_cache"], "WeatherCacheRepository", WHITE, NAVY, fnt=SMALL)

    draw.rounded_rectangle(external_group, 18, fill="#fffbeb", outline=AMBER, width=3)
    draw.text((95, 1070), "Persistence and external providers", font=LABEL, fill=AMBER)
    postgres = (190, 1110, 470, 1170)
    openweather = (760, 1110, 1040, 1170)
    llm_api = (1330, 1110, 1610, 1170)
    rounded_box(draw, postgres, "PostgreSQL", LIGHT_AMBER, AMBER, fnt=SMALL)
    rounded_box(draw, openweather, "OpenWeather API", LIGHT_AMBER, AMBER, fnt=SMALL)
    rounded_box(draw, llm_api, "LLM API", LIGHT_AMBER, AMBER, fnt=SMALL)

    labeled_arrow(draw, (330, 198), (450, 198))
    labeled_arrow(draw, (760, 198), (880, 198))
    labeled_arrow(draw, (1190, 198), (1360, 198), "DTO contracts")
    labeled_arrow(draw, (1190, 228), (1360, 355), "errors", RED)
    labeled_arrow(draw, (1035, 245), (630, 350), "calls services")
    labeled_arrow(draw, (630, 640), (630, 760), "uses")
    labeled_arrow(draw, (240, 890), (330, 1110))
    labeled_arrow(draw, (560, 890), (900, 1110))
    labeled_arrow(draw, (910, 890), (1470, 1110))
    labeled_arrow(draw, (1245, 890), (330, 1110))
    labeled_arrow(draw, (1555, 890), (330, 1110))
    save(image, "component-diagram.png")


def render_request_lifecycle() -> None:
    image, draw = base_canvas(
        1800,
        1920,
        "WeatherWear API - Request-Response Lifecycle",
        "Authentication, validation, service execution, external dependencies, persistence, and errors.",
    )
    start = (470, 140, 810, 230)
    jwt = (110, 300, 390, 390)
    controller = (470, 460, 810, 550)
    validate = (470, 660, 810, 750)
    service = (470, 1000, 810, 1090)
    persistence = (470, 1390, 810, 1480)
    success = (470, 1740, 810, 1830)
    external_apis = (1120, 1150, 1560, 1250)
    unauthorized = (80, 680, 420, 770)
    bad_request = (1120, 820, 1560, 920)
    bad_gateway = (1120, 1550, 1560, 1650)
    not_found = (1120, 1740, 1560, 1830)

    rounded_box(draw, start, "Client sends HTTP request to /api/...", LIGHT_BLUE, BLUE)
    diamond(draw, (640, 340), (360, 150), "Public endpoint?", LIGHT_AMBER, AMBER)
    rounded_box(draw, jwt, "JwtAuthFilter validates Bearer token", LIGHT_BLUE, BLUE)
    diamond(draw, (250, 520), (300, 140), "Token valid?", LIGHT_AMBER, AMBER)
    rounded_box(draw, controller, "Controller receives request", LIGHT_GREEN, GREEN)
    rounded_box(draw, validate, "Validate query, path, and JSON body", LIGHT_GREEN, GREEN)
    diamond(draw, (640, 870), (340, 150), "Valid request?", LIGHT_AMBER, AMBER)
    rounded_box(draw, service, "Service executes business workflow", LIGHT_GREEN, GREEN)
    diamond(draw, (640, 1210), (390, 150), "External dependency needed?", LIGHT_AMBER, AMBER)
    rounded_box(draw, external_apis, "OpenWeather API or LLM API", LIGHT_AMBER, AMBER)
    diamond(draw, (1340, 1400), (370, 150), "Dependency success?", LIGHT_AMBER, AMBER)
    rounded_box(draw, persistence, "Read or write PostgreSQL", LIGHT_GREEN, GREEN)
    diamond(draw, (640, 1600), (420, 150), "Resource exists and belongs to user?", LIGHT_AMBER, AMBER)
    rounded_box(draw, success, "2xx response DTO", LIGHT_GREEN, GREEN)
    rounded_box(draw, unauthorized, "401 Unauthorized", LIGHT_RED, RED)
    rounded_box(draw, bad_request, "400 Bad Request with ErrorResponse", LIGHT_RED, RED)
    rounded_box(draw, bad_gateway, "502 Bad Gateway with ErrorResponse", LIGHT_RED, RED)
    rounded_box(draw, not_found, "404 Not Found with ErrorResponse", LIGHT_RED, RED)

    labeled_arrow(draw, (640, 230), (640, 265))
    labeled_arrow(draw, (640, 415), (640, 460), "Yes")
    labeled_arrow(draw, (460, 340), (390, 345), "No")
    labeled_arrow(draw, (250, 390), (250, 450))
    labeled_arrow(draw, (250, 590), (250, 680), "No", RED)
    labeled_arrow(draw, (400, 520), (470, 505), "Yes")
    labeled_arrow(draw, (640, 550), (640, 660))
    labeled_arrow(draw, (810, 870), (1120, 870), "No", RED)
    labeled_arrow(draw, (640, 945), (640, 1000), "Yes")
    labeled_arrow(draw, (640, 1090), (640, 1135))
    labeled_arrow(draw, (835, 1210), (1120, 1200), "Yes")
    labeled_arrow(draw, (640, 1285), (640, 1390), "No")
    labeled_arrow(draw, (1340, 1250), (1340, 1325))
    labeled_arrow(draw, (1340, 1475), (1340, 1550), "No", RED)
    labeled_arrow(draw, (1155, 1400), (810, 1435), "Yes")
    labeled_arrow(draw, (640, 1480), (640, 1525))
    labeled_arrow(draw, (850, 1600), (1120, 1785), "No", RED)
    labeled_arrow(draw, (640, 1675), (640, 1740), "Yes")
    save(image, "request-lifecycle.png")


def participant_box(draw: ImageDraw.ImageDraw, x: int, label: str) -> tuple[int, int, int, int]:
    box = (x - 115, 150, x + 115, 235)
    rounded_box(draw, box, label, LIGHT_BLUE, BLUE, fnt=TEXT)
    return box


def sequence_arrow(
    draw: ImageDraw.ImageDraw,
    xs: dict[str, int],
    src: str,
    dst: str,
    y: int,
    label: str,
    dashed: bool = False,
) -> None:
    color = GREY if dashed else NAVY
    src_key = src.removesuffix("_return")
    dst_key = dst.removesuffix("_return")
    if dashed:
        x1, x2 = xs[src_key], xs[dst_key]
        step = 18 if x2 > x1 else -18
        x = x1
        while (x < x2 if step > 0 else x > x2):
            x_end = x + step * 0.55
            draw.line((x, y, x_end, y), fill=color, width=3)
            x += step
        arrow(draw, (x2 - (16 if x2 > x1 else -16), y), (x2, y), fill=color, width=3, head=10)
    else:
        arrow(draw, (xs[src_key], y), (xs[dst_key], y), fill=color, width=3, head=12)
    mid = (xs[src_key] + xs[dst_key]) // 2
    lines = wrap_text(draw, label, SMALL, abs(xs[dst_key] - xs[src_key]) - 30)
    text_h = len(lines) * 18
    label_top = y - text_h - 42
    draw.rounded_rectangle((mid - 170, label_top - 4, mid + 170, label_top + text_h + 8), 8, fill=WHITE)
    yy = label_top
    for line in lines:
        w, _ = text_size(draw, line, SMALL)
        draw.text((mid - w // 2, yy), line, font=SMALL, fill=color)
        yy += 18


def render_sequence(
    name: str,
    title: str,
    subtitle: str,
    participants: list[tuple[str, str]],
    messages: list[tuple[str, str, str, str]],
    blocks: list[tuple[str, int, int, str]],
    height: int,
) -> None:
    width = 1900
    image, draw = base_canvas(width, height, title, subtitle)
    xs = {key: 150 + i * ((width - 300) // (len(participants) - 1)) for i, (key, _) in enumerate(participants)}
    for key, label in participants:
        participant_box(draw, xs[key], label)
        draw.line((xs[key], 235, xs[key], height - 80), fill="#cbd5e1", width=2)

    for label, y1, y2, color in blocks:
        draw.rounded_rectangle((70, y1, width - 70, y2), 12, outline=color, width=3)
        draw.rectangle((90, y1 - 1, 260, y1 + 34), fill=WHITE)
        draw.text((105, y1 + 8), label, font=SMALL, fill=color)

    for src, dst, label, y_string in messages:
        y = int(y_string)
        sequence_arrow(draw, xs, src, dst, y, label, dashed=src.endswith("_return"))

    save(image, name)


def render_recommendation_sequence() -> None:
    render_sequence(
        "recommendation-sequence.png",
        "WeatherWear API - Recommendation Request Flow",
        "JWT authentication, weather cache lookup, AI generation, and history persistence.",
        [
            ("client", "Client"),
            ("auth", "JwtAuthFilter"),
            ("api", "RecommendationController"),
            ("weather", "WeatherService"),
            ("cache", "WeatherCacheRepository"),
            ("llm", "LlmClient"),
            ("db", "PostgreSQL"),
        ],
        [
            ("client", "auth", "POST /api/recommendations with Bearer token", "310"),
            ("auth", "api", "Authenticated request", "390"),
            ("api", "weather", "getWeather(city or coordinates)", "470"),
            ("weather", "cache", "find non-expired cached weather", "550"),
            ("cache_return", "weather", "cached weather", "640"),
            ("weather", "cache", "save weather cache row", "760"),
            ("api", "db", "load current user preferences", "870"),
            ("api", "llm", "generate clothing recommendation", "960"),
            ("api", "db", "save recommendation history", "1050"),
            ("api_return", "client", "200 RecommendationResponse", "1150"),
        ],
        [
            ("alt cache hit", 600, 690, GREEN),
            ("else cache miss", 720, 805, AMBER),
        ],
        1280,
    )


def render_chat_sequence() -> None:
    render_sequence(
        "chat-sequence.png",
        "WeatherWear API - Chat Request Flow",
        "Session handling, optional weather context, LLM answer, and message persistence.",
        [
            ("client", "Client"),
            ("api", "ChatController"),
            ("service", "ChatService"),
            ("db", "PostgreSQL"),
            ("weather", "WeatherService"),
            ("llm", "LlmClient"),
        ],
        [
            ("client", "api", "POST /api/chat with Bearer token", "310"),
            ("api", "service", "sendMessage(request)", "390"),
            ("service", "db", "get or create chat session", "470"),
            ("service", "db", "save USER message", "550"),
            ("service", "weather", "fetch current weather context", "670"),
            ("service", "db", "load last 10 messages", "790"),
            ("service", "llm", "generate assistant answer", "880"),
            ("service", "db", "save ASSISTANT message and update session", "970"),
            ("service_return", "api", "ChatResponse", "1080"),
            ("api_return", "client", "200 ChatResponse", "1170"),
        ],
        [
            ("opt city supplied", 620, 720, PURPLE),
        ],
        1300,
    )


def main() -> None:
    render_system_context()
    render_component_diagram()
    render_request_lifecycle()
    render_recommendation_sequence()
    render_chat_sequence()
    print(f"Rendered PNG diagrams to {OUT}")


if __name__ == "__main__":
    main()
