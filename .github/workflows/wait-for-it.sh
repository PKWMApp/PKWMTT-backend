# wait-for-it.sh
until nc -z -w5 mysql 3306; do
  echo "Waiting for MySQL..."
  sleep 1
done
echo "MySQL is ready!"
