.PHONY: help build install test lint release clean

GRADLEW := ./gradlew

help:
	@echo "Available commands:"
	@echo "  make build    - Assemble debug APK"
	@echo "  make install  - Install debug APK on connected device or emulator"
	@echo "  make test     - Run local unit tests"
	@echo "  make lint     - Run Android lint analysis"
	@echo "  make release  - Assemble release APK"
	@echo "  make clean    - Clean build cache and build directory"

build:
	$(GRADLEW) assembleDebug

install:
	$(GRADLEW) installDebug

test:
	$(GRADLEW) test

lint:
	$(GRADLEW) lint

release:
	$(GRADLEW) assembleRelease

clean:
	$(GRADLEW) clean
