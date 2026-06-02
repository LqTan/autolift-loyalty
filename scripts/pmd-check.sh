#!/bin/bash

echo "=== Running PMD Check ==="
./mvnw pmd:pmd -q 2>/dev/null

if [ -f target/pmd.xml ]; then
    echo ""
    echo "=== PMD Results ==="
    echo ""

    TOTAL=$(grep -c '<violation' target/pmd.xml 2>/dev/null || echo "0")
    echo "Total violations: $TOTAL"
    echo ""

    python3 << 'EOF'
import xml.etree.ElementTree as ET

ns = {'pmd': 'http://pmd.sourceforge.net/report/2.0.0'}
tree = ET.parse('target/pmd.xml')

for f in tree.findall('.//pmd:file', ns):
    filepath = f.get('name')
    filename = '/'.join(filepath.split('/')[-2:])
    print(f'[{filename}]')
    for v in f.findall('pmd:violation', ns):
        line = v.get('beginline')
        rule = v.get('rule')
        msg = (v.text or '').strip()
        print(f'   Line {line}: [{rule}]')
        if msg:
            print(f'   - {msg}')
    print()
EOF

    echo "=== Done ==="
else
    echo "No PMD report generated"
fi
