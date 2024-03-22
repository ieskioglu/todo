FROM couchbase:latest

# Copy the setup script into the container
COPY setup_couchbase.sh /opt/couchbase/init/setup_couchbase.sh

# Set execute permission for the script
RUN chmod +x /opt/couchbase/init/setup_couchbase.sh

# Start the Couchbase server and run the setup script
CMD ["/opt/couchbase/init/setup_couchbase.sh"]