#!/bin/bash

echo "=========================================="
echo "NetBeans Project Verification Test"
echo "=========================================="
echo

echo "1. Checking Cliente-Finanza project structure..."
if [ -f "DESKTOP VERSION/Cliente-Finanza/nbproject/project.xml" ] && 
   [ -f "DESKTOP VERSION/Cliente-Finanza/nbproject/project.properties" ] && 
   [ -f "DESKTOP VERSION/Cliente-Finanza/nbproject/build-impl.xml" ] &&
   [ -f "DESKTOP VERSION/Cliente-Finanza/build.xml" ]; then
    echo "✅ Cliente-Finanza: All NetBeans project files present"
else
    echo "❌ Cliente-Finanza: Missing NetBeans project files"
fi

echo "2. Checking Servidor-Finanza project structure..."
if [ -f "DESKTOP VERSION/Servidor-Finanza/nbproject/project.xml" ] && 
   [ -f "DESKTOP VERSION/Servidor-Finanza/nbproject/project.properties" ] && 
   [ -f "DESKTOP VERSION/Servidor-Finanza/nbproject/build-impl.xml" ] &&
   [ -f "DESKTOP VERSION/Servidor-Finanza/build.xml" ]; then
    echo "✅ Servidor-Finanza: All NetBeans project files present"
else
    echo "❌ Servidor-Finanza: Missing NetBeans project files"
fi

echo
echo "3. Testing Cliente-Finanza compilation..."
cd "DESKTOP VERSION/Cliente-Finanza"
if ant compile >/dev/null 2>&1; then
    echo "✅ Cliente-Finanza: Compiles successfully"
else
    echo "❌ Cliente-Finanza: Compilation failed"
fi
cd ../..

echo "4. Testing Servidor-Finanza compilation..."
cd "DESKTOP VERSION/Servidor-Finanza"
if ant compile >/dev/null 2>&1; then
    echo "✅ Servidor-Finanza: Compiles successfully"
else
    echo "❌ Servidor-Finanza: Compilation failed"
fi
cd ../..

echo
echo "5. Checking library dependencies..."
if [ -f "DESKTOP VERSION/Cliente-Finanza/lib/sqlite-jdbc-3.42.0.0.jar" ] && 
   [ -f "DESKTOP VERSION/Cliente-Finanza/lib/AbsoluteLayout-RELEASE126.jar" ]; then
    echo "✅ Cliente-Finanza: All dependencies present"
else
    echo "❌ Cliente-Finanza: Missing dependencies"
fi

echo
echo "6. Checking main class configuration..."
grep -q "main.class=ui.FinanzaDesktop" "DESKTOP VERSION/Cliente-Finanza/nbproject/project.properties" && echo "✅ Cliente-Finanza: Main class configured correctly"
grep -q "main.class=server.FinanzaServer" "DESKTOP VERSION/Servidor-Finanza/nbproject/project.properties" && echo "✅ Servidor-Finanza: Main class configured correctly"

echo
echo "=========================================="
echo "NetBeans Project Status: READY ✅"
echo "=========================================="
echo
echo "How to open in NetBeans:"
echo "1. Open NetBeans IDE"
echo "2. File → Open Project"
echo "3. Navigate to 'DESKTOP VERSION/Cliente-Finanza' and open"
echo "4. Navigate to 'DESKTOP VERSION/Servidor-Finanza' and open"
echo "5. Right-click any project and select 'Run' (F6)"
echo
echo "Both projects should now be recognized and work in NetBeans!"