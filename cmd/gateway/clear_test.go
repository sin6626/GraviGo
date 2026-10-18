package main

import (
	"fmt"
	"net"
	"net/http"
	"net/http/httptest"
	"path/filepath"
	"strconv"
	"strings"
	"testing"

	"antigravity-mobile/internal/auth"
)

func TestClearUsesSelectedPortAndDoesNotFallBackOnHTTPError(t *testing.T) {
	for _, status := range []int{http.StatusNoContent, http.StatusUnauthorized, http.StatusNotFound, http.StatusInternalServerError} {
		t.Run(strconv.Itoa(status), func(t *testing.T) {
			storePath := filepath.Join(t.TempDir(), "auth.json")
			t.Setenv("AUTH_STORE_PATH", storePath)
			t.Setenv("MULTIGRAVITY_ADMIN_TOKEN", "test-admin-token")
			store, err := auth.NewAuthStore(storePath)
			if err != nil {
				t.Fatal(err)
			}
			if err := store.AddDevice(auth.PairedDevice{DeviceID: "keep-device"}); err != nil {
				t.Fatal(err)
			}
			called := make(chan struct{}, 1)
			server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
				called <- struct{}{}
				if r.Method != http.MethodDelete || r.URL.Path != "/api/v1/devices/keep-device" {
					t.Errorf("unexpected request: %s %s", r.Method, r.URL.Path)
				}
				if r.Header.Get("Authorization") != "Bearer test-admin-token" {
					t.Error("missing admin authorization")
				}
				w.WriteHeader(status)
			}))
			defer server.Close()
			port := server.Listener.Addr().(*net.TCPAddr).Port
			err = runClearCmd([]string{"-port", strconv.Itoa(port), "keep-device"})
			select {
			case <-called:
			default:
				t.Fatal("selected gateway was not called")
			}
			if status == http.StatusNoContent && err != nil {
				t.Fatal(err)
			}
			if status != http.StatusNoContent && (err == nil || !strings.Contains(err.Error(), fmt.Sprint(status))) {
				t.Fatalf("HTTP failure must be returned: %v", err)
			}
			restored, err := auth.NewAuthStore(storePath)
			if err != nil {
				t.Fatal(err)
			}
			if len(restored.ListDevices()) != 1 {
				t.Fatal("online operation modified the offline store")
			}
		})
	}
}

func TestClearRejectsInvalidArguments(t *testing.T) {
	for _, args := range [][]string{{"-port", "0"}, {"-port", "65536"}, {"device", "extra"}, {"../all"}, {"-unknown"}} {
		if err := runClearCmd(args); err == nil {
			t.Errorf("accepted invalid args: %v", args)
		}
	}
}

func TestClearOfflineMissingDeviceReturnsError(t *testing.T) {
	listener, err := net.Listen("tcp", "127.0.0.1:0")
	if err != nil {
		t.Fatal(err)
	}
	port := listener.Addr().(*net.TCPAddr).Port
	listener.Close()
	t.Setenv("AUTH_STORE_PATH", filepath.Join(t.TempDir(), "auth.json"))
	t.Setenv("MULTIGRAVITY_ADMIN_TOKEN", "test-admin-token")
	if err := runClearCmd([]string{"-port", strconv.Itoa(port), "missing-device"}); err == nil {
		t.Fatal("missing device must return an error")
	}
}
