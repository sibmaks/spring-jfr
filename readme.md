# Диагностическая инструментация Spring Boot с помощью Java Flight Recorder

<!-- TOC -->
* [Диагностическая инструментация Spring Boot с помощью Java Flight Recorder](#диагностическая-инструментация-spring-boot-с-помощью-java-flight-recorder)
  * [Цель проекта](#цель-проекта)
  * [Задачи проекта](#задачи-проекта)
  * [Плюсы и минусы разных подходов](#плюсы-и-минусы-разных-подходов)
<!-- TOC -->

## Цель проекта

Создание библиотеки для Spring, которая использует Java Flight Recorder (JFR) для мониторинга и трассировки ключевых
процессов фреймворка Spring,
таких как:

* инициализация контекста;
* создание бинов;
* входящие HTTP-запросы;
* вызовы Spring Rest и Spring Data;
* использование пулов потоков и планировщиков (`Scheduler`).


## Задачи проекта

1. **Исследование возможностей Java Flight Recorder (JFR) и Spring**:
    - Изучение возможностей JFR по созданию пользовательских событий и их интеграции с приложениями.
    - Изучение ключевых механизмов Spring, которые могут быть инструментированы (Bean Lifecycle, Spring Context,
      RestControllers, Data Repositories, Thread Pools, Scheduled Tasks).

2. **Разработка архитектуры модуля для интеграции JFR со Spring**:
    - Определение основных точек интеграции для трассировки Spring (BeanPostProcessor, фильтры для HTTP-запросов, пул
      потоков, работа планировщиков).
    - Определение типов событий, которые будут генерироваться: создание бинов, зависимости, выполнение запросов,
      использование потоков.

3. **Рассмотрение методов генерации событий JFR**:
    - **Bean Post Processor**: инструментирование на уровне жизненного цикла бинов (инициализация, уничтожение).
        - *Плюсы*: доступ к жизненному циклу бинов напрямую, низкая сложность.
        - *Минусы*: охватывает только бины, нет возможности инструментировать весь код.
    - **AspectJ (AOP)**: инструментирование через аспекты для мониторинга вызовов методов.
        - *Плюсы*: гибкость, возможность трассировать любые методы (например, контроллеры, репозитории).
        - *Минусы*: накладные расходы на выполнение аспектов, не всегда совместимо с глубокой оптимизацией
          JIT-компилятора.
    - **Инструментация байт-кода (Bytecode Instrumentation)**: использование таких инструментов, как ASM, ByteBuddy для
      вставки кода во время загрузки классов.
        - *Плюсы*: возможность глубокого контроля и инструментирования без изменения исходного кода.
        - *Минусы*: сложность реализации, более высокий риск ошибок, сложность отладки.

4. **Инструментирование Spring-пулов потоков и планировщиков**:
    - Инструментирование пулы потоков (`ThreadPoolTaskExecutor`, `ScheduledExecutorService`) для мониторинга
      использования потоков, блокировок, времени ожидания и времени выполнения задач.
    - Инструментирование планировщиков задач (`@Scheduled`, `TaskScheduler`) для отслеживания выполнения планируемых
      задач, возможных задержек и перегрузок.

5. **Разработка пользовательских JFR-событий**:
    - Определение пользовательских событий (события для трассировки времени инициализации бинов, времени
      выполнения HTTP-запросов, времени выполнения задач пула потоков).

6. **Создание Spring Boot Starter для JFR-инструментации**:
    - Реализация библиотеки или Spring Boot Starter, который автоматически подключает все необходимые зависимости и
      настраивает инструментирование при старте приложения.

7. **Тестирование и оптимизация**:
    - Проведение нагрузочных тестов для оценки влияния инструментирования на производительность приложения.
    - Оптимизация мест, где возможны потери производительности, и настройка JFR для минимизации накладных расходов.

## Плюсы и минусы разных подходов

| Метод                                   | Плюсы                                                                | Минусы                                                                                |
|-----------------------------------------|----------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| **Bean Post Processor**                 | Легкая реализация, поддержка жизненного цикла бинов                  | Ограниченная область применения (только бины), отсутствие трассировки вызовов методов |
| **AspectJ (AOP)**                       | Гибкость, возможность инструментирования любых методов               | Накладные расходы на аспекты, сложнее производить оптимизацию                         |
| **Байт-код (Bytecode Instrumentation)** | Глубокая интеграция на уровне байт-кода, высокая детализация событий | Сложность реализации, повышенная вероятность ошибок, сложность тестирования           |
