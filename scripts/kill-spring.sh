#!/bin/bash
pkill -f "spring-boot:run" 2>/dev/null
pkill -f "BootWar" 2>/dev/null
sleep 1
echo "Killed Spring Boot processes"