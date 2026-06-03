# Unit Tests Report — Uroboros

**Дата обновления:** 31.05.2026  
**Команда запуска:** `./gradlew :app:testDebugUnitTest`

## Итог

| Метрика | Значение |
|---------|----------|
| Всего тестов | 71 |
| Пройдено | 71 |
| Провалено | 0 |

---

## Новые Unit-тесты (май 2026)

| № | Название теста | Описание | Ожидаемый результат | Фактический результат |
|---|----------------|----------|---------------------|------------------------|
| 1 | `ReminderIntervalsTest.normalize returns default for empty list` | Пустой список интервалов заменяется значением по умолчанию | `[1, 3, 7, 14]` | Пройден ✓ |
| 2 | `ReminderIntervalsTest.normalize sorts distinct valid days` | Дубликаты удаляются, дни сортируются по возрастанию | `[1, 3, 7, 14]` | Пройден ✓ |
| 3 | `ReminderIntervalsTest.normalize filters out of range values` | Значения вне диапазона 1–365 отбрасываются | `[1, 365]` | Пройден ✓ |
| 4 | `ReminderIntervalsTest.normalize limits to max count` | Не более 8 интервалов сохраняется | Размер списка = 8 | Пройден ✓ |
| 5 | `ReminderIntervalsTest.parse returns default for null` | При `null` в настройках возвращаются интервалы по умолчанию | `[1, 3, 7, 14]` | Пройден ✓ |
| 6 | `ReminderIntervalsTest.serialize and parse roundtrip` | Сериализация и парсинг интервалов обратимы | `"2,5,10"` → `[2, 5, 10]` | Пройден ✓ |
| 7 | `ReminderTimeTest.format pads hour and minute` | Время форматируется с ведущими нулями | `"09:05"` | Пройден ✓ |
| 8 | `ReminderTimeTest.default reminder time is 09-00` | Значение по умолчанию — 9:00 | hour=9, minute=0 | Пройден ✓ |
| 9 | `ReminderTimeTest.targetMillis adds interval days and sets time of day` | Расчёт даты напоминания: база + N дней + время | 13.01.2026 14:30 | Пройден ✓ |
| 10 | `NotesBackupTest.toJson and fromJson preserve notes and tags` | Экспорт/импорт JSON сохраняет заметки и метки | Данные идентичны исходным | Пройден ✓ |
| 11 | `NotesBackupTest.fromJson includes version field` | JSON-бэкап содержит поле `version` | `"version": 1` в JSON | Пройден ✓ |
| 12 | `NotesBackupTest.fromJson rejects unsupported version` | Неподдерживаемая версия бэкапа отклоняется | `IllegalArgumentException` | Пройден ✓ |
| 13 | `LlmQuizResponseParserTest.parse extracts JSON from markdown fenced block` | Парсер извлекает JSON из ответа LLM с обёрткой ` ```json ` | Объект с question, options, correct_index | Пройден ✓ |
| 14 | `LlmQuizResponseParserTest.parse returns null for invalid JSON` | Невалидный JSON не ломает приложение | `null` | Пройден ✓ |
| 15 | `LlmErrorMapperTest.toApiException parses error field from JSON body` | HTTP 403 с телом `{"error":"..."}` преобразуется в `LlmApiException` | code=403, сообщение содержит «Permission denied» | Пройден ✓ |
| 16 | `LlmErrorMapperTest.isNetworkIssue detects UnknownHostException` | Ошибка DNS классифицируется как сетевая | `true` | Пройден ✓ |
| 17 | `LlmErrorMapperTest.isNetworkIssue detects SocketTimeoutException` | Таймаут классифицируется как сетевая | `true` | Пройден ✓ |
| 18 | `LlmErrorMapperTest.isNetworkIssue returns false for LlmApiException` | HTTP-ошибка API не считается сетевой | `false` | Пройден ✓ |
| 19 | `LlmErrorMapperTest.isNetworkIssue returns true for generic IOException` | Общий `IOException` — сетевая ошибка | `true` | Пройден ✓ |
| 20 | `ReminderIntentParserTest.isOpenNoteIntent returns true for OPEN_NOTE action` | Intent с action `OPEN_NOTE` распознаётся | `true` | Пройден ✓ |
| 21 | `ReminderIntentParserTest.isOpenNoteIntent returns false for other action` | Другие action не считаются открытием материала | `false` | Пройден ✓ |
| 22 | `ReminderIntentParserTest.parseNoteId returns id from valid intent` | Из intent извлекается ID материала | `42L` | Пройден ✓ |
| 23 | `ReminderIntentParserTest.parseNoteId returns null when id missing` | Без extra `note_id` возвращается null | `null` | Пройден ✓ |
| 24 | `ReminderIntentParserTest.parseNoteId returns null for non reminder intent` | Intent без action OPEN_NOTE игнорируется | `null` | Пройден ✓ |
| 25 | `MaterialQuizTest.options returns all four choices in order` | Метод `options()` возвращает 4 варианта ответа | `[One, Two, Three, Four]` | Пройден ✓ |
| 26 | `KnowledgeCheckGeneratorTest.generate returns plain segment for blank text` | Пустой текст — один Plain-сегмент | 1 сегмент типа Plain | Пройден ✓ |
| 27 | `KnowledgeCheckGeneratorTest.generate hides words deterministically for same seed` | Один seed даёт одинаковый результат | Списки сегментов равны | Пройден ✓ |
| 28 | `KnowledgeCheckGeneratorTest.generate contains at least one hidden word for long text` | В длинном тексте есть скрытые слова | Есть `HiddenWord` | Пройден ✓ |
| 29 | `KnowledgeCheckGeneratorTest.reconstructed text matches original` | Сборка сегментов восстанавливает исходный текст | Тексты совпадают | Пройден ✓ |
| 30 | `QuizGenerationLimiterTest.remainingToday starts at daily limit` | В начале дня доступно 3 генерации | `remainingToday() == 3` | Пройден ✓ |
| 31 | `QuizGenerationLimiterTest.tryConsumeGeneration decreases remaining count` | После генерации счётчик уменьшается | Осталось 2 | Пройден ✓ |
| 32 | `QuizGenerationLimiterTest.tryConsumeGeneration returns false after limit reached` | После 3 генераций четвёртая блокируется | `false`, осталось 0 | Пройден ✓ |

---

## Файлы новых тестов

```
app/src/test/java/com/reysl/uroboros/
├── data/
│   ├── MaterialQuizTest.kt
│   └── preferences/
│       ├── ReminderIntervalsTest.kt
│       ├── ReminderTimeTest.kt
│       └── QuizGenerationLimiterTest.kt
├── data/llm/
│   ├── LlmErrorMapperTest.kt
│   └── LlmQuizResponseParserTest.kt
├── notification/
│   └── ReminderIntentParserTest.kt
└── utils/
    ├── KnowledgeCheckGeneratorTest.kt
    └── NotesBackupTest.kt
```

---

## Покрытые области (новые)

| Область | Что проверяется |
|---------|-----------------|
| **Напоминания** | Нормализация интервалов, расчёт времени срабатывания |
| **Экспорт/импорт** | JSON-бэкап заметок и меток |
| **AI Quiz** | Парсинг ответа LLM, маппинг HTTP-ошибок, лимит 3/день |
| **Уведомления** | Deep link intent из push |
| **Проверка знаний** | Детерминированное скрытие слов |
| **Quiz UI model** | Варианты ответа MaterialQuiz |

---

## Технологии

- JUnit 4
- Mockito / mockito-kotlin
- Robolectric 4.11 (Intent, JSONObject)
- kotlinx-coroutines-test
- androidx.arch.core:core-testing

---

## Ранее существующие тесты (39 шт.)

- `NoteTest.kt` — 6 тестов
- `TagTest.kt` — 7 тестов
- `NoteDaoTest.kt` — 13 тестов
- `NoteRepositoryTest.kt` — 4 теста
- `NoteViewModelTest.kt` — 9 тестов

Подробности — в истории отчёта от 30.04.2026.

---

## Запуск

```bash
./gradlew :app:testDebugUnitTest
```

Отчёт HTML: `app/build/reports/tests/testDebugUnitTest/index.html`
