
import os
import sys
import io
import yfinance as yf
import pandas as pd
from minio import Minio
from minio.error import S3Error

if len(sys.argv) < 2:
    raise Exception("The argument required: path to minio folder")

minio_channel = sys.argv[1]
minio_endpoint = os.getenv("MINIO_ENDPOINT", "localhost:9000")
minio_access_key = os.getenv("MINIO_ACCESS_KEY", "minioadmin")
minio_secret_key = os.getenv("MINIO_SECRET_KEY", "minioadmin")
minio_bucket = os.getenv("MINIO_BUCKET", "ilum-files") 

minio_path = os.getenv("MINIO_PATH", "")


client = Minio(
    endpoint=minio_endpoint,
    access_key=minio_access_key,
    secret_key=minio_secret_key,
    secure=False
)


ticker_symbol = 'ETH-BTC'
start_date = '2021-01-01'
end_date = '2023-01-01'

object_name = 'ethbtc_2021-2023.csv'

data = yf.download(ticker_symbol, start=start_date, end=end_date, interval="1d")

csv_buffer = io.StringIO()
data.to_csv(csv_buffer)



csv_data = csv_buffer.getvalue()


try:
    client.put_object(
        minio_bucket,
        f'jobs-manager/{minio_channel}/{object_name}',
        io.BytesIO(csv_data.encode('utf-8')),
        len(csv_data),
        content_type='text/csv'
    )
    print(f"Data uploaded to bucket {minio_bucket} as {object_name}")
except S3Error as e:
    print(f"Error occurred: {e}")